// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

contract DurianChain {
    enum Status { Created, Ordered, Collected, Delivered, Deleted }
    enum Role { Unknown, Admin, Farmer, Trader, Logistics }

    struct Durian {
        string id;
        string imageHash;
    }

    struct Batch {
        string batchId;
        string variety;
        string farmId;
        uint256 quantity;
        string farmLocation;
        Status status;
        address farmer;
        address trader;
        address logistics;
        string traderAgencyId;
        string logisticsCompanyId;
        string batchImageCID;
        string deliveryDestination;
        Durian[] durians;
    }

    struct Farm {
        string farmId;
        address owner;
        string location;
        string certificateCID;
        uint256 certificateExpiry;
    }

    struct Transition {
        address actor;
        Role role;
        string action;
        string description;
        uint256 timestamp;
    }

    struct TraderAgency {
        string agencyId;
        address owner;
        string agencyName;
        string exportLicenseCID;
        uint256 exportLicenseExpiry;
    }

    struct LogisticsCompany {
        string companyId;
        address owner;
        string companyName;
    }

    address public superAdmin;
    mapping(address => bool) public isAdmin;
    mapping(address => Role) public roles;

    mapping(string => Farm) private farms;                // farmId => Farm
    mapping(address => string[]) private farmsByOwner;    // farmer => list of farmIds
    mapping(string => Transition[]) private farmHistory; // farmId => history logs

    mapping(string => Batch) private batches;
    mapping(address => string[]) private batchesByFarmer;           // batchId => Batch
    mapping(string => Transition[]) private batchHistory; // batchId => Transitions

    mapping(string => TraderAgency) private traderAgencies;           // agencyId => TraderAgency
    mapping(address => string[]) private traderAgenciesByOwner;
    mapping(string => Transition[]) private agencyHistory;                  // agencyId => Transition[]

    mapping(string => LogisticsCompany) private logisticsCompanies;            // companyId => company
    mapping(address => string[]) private logisticsByOwner;                     // owner => list of companyIds
    mapping(string => Transition[]) private logisticsHistory;                  // for milestone logs


    event RoleAssigned(address indexed user, Role role);
    event AdminRemoved(address indexed user);
    event TransitionLogged(string batchId, address actor, Role role, string action, string description, uint256 timestamp);

    constructor() {
        superAdmin = msg.sender;
        isAdmin[msg.sender] = true;
        roles[msg.sender] = Role.Admin;
    }

    // ---------------- Access Control ----------------

    modifier onlySuperAdmin() {
        // require(msg.sender == superAdmin, "Only super admin");
        _;
    }

    modifier onlyAdmin() {
        // require(isAdmin[msg.sender], "Only admin");
        _;
    }

    modifier onlyRole(Role r) {
        // require(roles[msg.sender] == r, "Unauthorized role");
        _;
    }

    function assignRole(address user, Role role) external onlyAdmin {
        require(user != address(0), "Invalid address");
        require(user != superAdmin, "Cannot change role of superAdmin");

        roles[user] = role;
        isAdmin[user] = (role == Role.Admin);

        emit RoleAssigned(user, role);
    }

    function getUserRole(address user) external view onlyAdmin returns (Role) {
        return roles[user];
    }

    function getMyRoleAsString() external view returns (string memory) {
        Role r = roles[msg.sender];
        return _roleToString(r);
    }

    // ---------------- Farmer Functions ----------------

    function registerFarm(
        string memory farmId,
        string memory location,
        string memory certificateCID,
        uint256 certificateExpiry
    ) external onlyRole(Role.Farmer) {
        require(bytes(farmId).length > 0, "Farm ID required");
        require(bytes(location).length > 0, "Location required");
        require(bytes(farms[farmId].farmId).length == 0, "Farm ID already used");

        farms[farmId] = Farm({
            farmId: farmId,
            owner: msg.sender,
            location: location,
            certificateCID: certificateCID,
            certificateExpiry: certificateExpiry
        });

        farmsByOwner[msg.sender].push(farmId);

        _logFarmTransition(farmId, "Farm Registered", "Farmer registered a new farm", Role.Farmer);
    }

    function getMyFarmIds() external view onlyRole(Role.Farmer) returns (string[] memory) {
        return farmsByOwner[msg.sender];
    }

    function updateFarmCertificate(
        string memory farmId,
        string memory newCertificateCID,
        uint256 newExpiry
    ) external onlyRole(Role.Farmer) {
        Farm storage f = farms[farmId];
        require(f.owner == msg.sender, "Not your farm");
        require(bytes(newCertificateCID).length > 0, "New CID required");
        require(newExpiry > block.timestamp, "Expiry must be future");

        string memory oldCID = f.certificateCID;
        uint256 oldExpiry = f.certificateExpiry;

        f.certificateCID = newCertificateCID;
        f.certificateExpiry = newExpiry;

        string memory description = string(
            abi.encodePacked(
                oldCID, " [", _uintToString(oldExpiry),
                "] update -> ", newCertificateCID, " [", _uintToString(newExpiry), "]"
            )
        );

        _logFarmTransition(farmId, "Certificate Updated", description, Role.Farmer);
    }

    function createBatch(
        string memory batchId,
        string memory variety,
        uint256 quantity,
        string memory farmId,
        string memory batchImageCID,
        string[] memory durianIds,
        string[] memory imageHashes
    ) external onlyRole(Role.Farmer) {
        require(bytes(batchId).length > 0, "Batch ID required");
        require(bytes(variety).length > 0, "Food name required");
        require(quantity > 0, "Quantity must be > 0");
        require(bytes(batchImageCID).length > 0, "Batch image CID required");
        require(bytes(farms[farmId].farmId).length > 0, "Farm not registered");
        require(farms[farmId].owner == msg.sender, "You do not own this farm");
        require(batches[batchId].quantity == 0, "Batch already exists");
        require(durianIds.length == imageHashes.length, "Mismatch durians/images");

        Batch storage b = batches[batchId];
        b.batchId = batchId;
        b.variety = variety;
        b.farmId = farmId;
        b.quantity = quantity;
        b.farmLocation = farms[farmId].location;
        b.status = Status.Created;
        b.farmer = msg.sender;
        b.batchImageCID = batchImageCID;

        for (uint i = 0; i < durianIds.length; i++) {
            require(bytes(durianIds[i]).length > 0, "Durian ID empty");
            require(bytes(imageHashes[i]).length > 0, "Durian image hash empty");
            b.durians.push(Durian(durianIds[i], imageHashes[i]));
        }

        batchesByFarmer[msg.sender].push(batchId);

        _logTransition(batchId, "Batch Created", "Farmer created a new durian batch", Role.Farmer);
    }

    function getMyBatchIds() external view onlyRole(Role.Farmer) returns (string[] memory) {
        return batchesByFarmer[msg.sender];
    }

    function updateBatchInfo(
        string memory batchId,
        uint256 newQuantity,
        string memory newBatchImageCID,
        string[] memory newDurianIds,
        string[] memory newImageHashes
    ) external onlyRole(Role.Farmer) {
        Batch storage b = batches[batchId];
        require(bytes(b.batchId).length > 0, "Batch does not exist");
        require(b.farmer == msg.sender, "Not your batch");
        require(b.status == Status.Created, "Batch must be in 'Created' state (not ordered or deleted)");
        require(newQuantity > 0, "Quantity must be > 0");
        require(bytes(newBatchImageCID).length > 0, "Image CID required");

        uint256 oldQuantity = b.quantity;
        string memory oldCID = b.batchImageCID;
        uint256 oldDurianCount = b.durians.length;

        b.quantity = newQuantity;
        b.batchImageCID = newBatchImageCID;

        if (newDurianIds.length > 0 || newImageHashes.length > 0) {
            require(newDurianIds.length == newImageHashes.length, "Mismatch durians/images");
            delete b.durians;
            for (uint i = 0; i < newDurianIds.length; i++) {
                require(bytes(newDurianIds[i]).length > 0, "Durian ID empty");
                require(bytes(newImageHashes[i]).length > 0, "Durian image hash empty");
                b.durians.push(Durian(newDurianIds[i], newImageHashes[i]));
            }
        }

        string memory description = "";

        if (oldQuantity != newQuantity) {
            description = string(
                abi.encodePacked(description,
                    "Quantity: ", _uintToString(oldQuantity), " -> ", _uintToString(newQuantity), ", ")
            );
        }

        if (keccak256(bytes(oldCID)) != keccak256(bytes(newBatchImageCID))) {
            description = string(
                abi.encodePacked(description,
                    "ImageCID: ", oldCID, " -> ", newBatchImageCID, ", ")
            );
        }

        if (oldDurianCount != b.durians.length) {
            description = string(
                abi.encodePacked(description,
                    "Durians: ", _uintToString(oldDurianCount), " -> ", _uintToString(b.durians.length))
            );
        }

        _logTransition(batchId, "Batch Info Updated", description, Role.Farmer);
    }

    function removeBatch(string memory batchId) external onlyRole(Role.Farmer) {
        Batch storage b = batches[batchId];

        require(bytes(b.batchId).length > 0, "Batch does not exist");
        require(b.farmer == msg.sender, "Not your batch");
        require(b.status == Status.Created, "Only unprocessed batch can be removed");

        b.status = Status.Deleted;

        _logTransition(batchId, "Batch Deleted", "Farmer marked batch as deleted", Role.Farmer);
    }

    // ---------------- Trader Functions ----------------

    function createTraderAgency(
        string memory agencyId,
        string memory agencyName,
        string memory exportLicenseCID,
        uint256 exportLicenseExpiry
    ) external onlyRole(Role.Trader) {
        require(bytes(agencyId).length > 0, "Agency ID required");
        require(bytes(agencyName).length > 0, "Agency name required");
        require(bytes(traderAgencies[agencyId].agencyId).length == 0, "Agency ID already exists");

        traderAgencies[agencyId] = TraderAgency({
            agencyId: agencyId,
            owner: msg.sender,
            agencyName: agencyName,
            exportLicenseCID: exportLicenseCID,
            exportLicenseExpiry: exportLicenseExpiry
        });

        traderAgenciesByOwner[msg.sender].push(agencyId);

        string memory desc = string(
            abi.encodePacked("Created agency: ", agencyName, ", Export CID: ", exportLicenseCID)
        );

        _logAgencyTransition(agencyId, "Agency Created", desc, Role.Trader);
    }

    function getMyTraderAgencyIds() external view onlyRole(Role.Trader) returns (string[] memory) {
        return traderAgenciesByOwner[msg.sender];
    }

    function updateTraderAgencyCertificate(
        string memory agencyId,
        string memory newExportCID,
        uint256 newExportExpiry
    ) external onlyRole(Role.Trader) {
        TraderAgency storage agency = traderAgencies[agencyId];

        require(agency.owner == msg.sender, "Not your agency");
        require(bytes(newExportCID).length > 0, "Export CID required");
        require(newExportExpiry > block.timestamp, "Expiry must be in the future");

        string memory oldCID = agency.exportLicenseCID;
        uint256 oldExpiry = agency.exportLicenseExpiry;

        agency.exportLicenseCID = newExportCID;
        agency.exportLicenseExpiry = newExportExpiry;

        string memory cidDisplay = bytes(oldCID).length > 0 ? oldCID : "N/A";

        string memory description = string(
            abi.encodePacked(
                "CID: ", cidDisplay, " -> ", newExportCID,
                ", Expiry: ", _uintToString(oldExpiry), " -> ", _uintToString(newExportExpiry)
            )
        );

        _logAgencyTransition(agencyId, "Export Certificate Updated", description, Role.Trader);
    }

    function orderBatch(
        string memory batchId,
        string memory agencyId,
        string memory deliveryDestination,
        bool isExport
    ) external onlyRole(Role.Trader) {
        Batch storage b = batches[batchId];
        require(b.status == Status.Created, "Only created batch can be ordered");
        require(bytes(deliveryDestination).length > 0, "Delivery destination required");

        TraderAgency storage agency = traderAgencies[agencyId];
        require(bytes(agency.agencyId).length > 0, "Trader agency not registered");
        require(agency.owner == msg.sender, "Not your agency");

        if (isExport) {
            require(bytes(agency.exportLicenseCID).length > 0, "Missing export license");
            require(agency.exportLicenseExpiry > block.timestamp, "Export license expired");

            Farm storage f = farms[b.farmId];
            require(bytes(f.certificateCID).length > 0, "Missing farm certificate");
            require(f.certificateExpiry > block.timestamp, "Farm certificate expired");
        }

        b.trader = msg.sender;
        b.traderAgencyId = agencyId;
        b.deliveryDestination = deliveryDestination;
        b.status = Status.Ordered;

        string memory desc = string(
            abi.encodePacked(
                "Batch ordered by agency ", agencyId,
                ", Delivery to: ", deliveryDestination,
                isExport ? ", [Export: validated]" : ", [Local Order]"
            )
        );

        _logTransition(batchId, "Batch Ordered", desc, Role.Trader);
    }

    function cancelOrder(string memory batchId) external onlyRole(Role.Trader) {
        Batch storage b = batches[batchId];

        require(bytes(b.batchId).length > 0, "Batch does not exist");
        require(b.status == Status.Ordered, "Only ordered batch can be cancelled");
        require(b.trader == msg.sender, "You did not order this batch");

        b.trader = address(0);
        b.traderAgencyId = "";
        b.status = Status.Created;

        _logTransition(batchId, "Order Cancelled", "Trader cancelled the order", Role.Trader);
    }

    // ---------------- Logistics Functions ----------------

    function registerLogisticsCompany(
        string memory companyId,
        string memory companyName
    ) external onlyRole(Role.Logistics) {
        require(bytes(companyId).length > 0, "Company ID required");
        require(bytes(companyName).length > 0, "Company name required");
        require(bytes(logisticsCompanies[companyId].companyId).length == 0, "Company ID already used");

        logisticsCompanies[companyId] = LogisticsCompany({
            companyId: companyId,
            owner: msg.sender,
            companyName: companyName
        });

        logisticsByOwner[msg.sender].push(companyId);

        _logLogisticsTransition(
            companyId,
            "Logistics Company Registered",
            "Logistics provider registered a company",
            Role.Logistics
        );
    }

    function getMyLogisticsCompanyIds() external view onlyRole(Role.Logistics) returns (string[] memory) {
        return logisticsByOwner[msg.sender];
    }

    function collectBatch(string memory batchId, string memory logisticsCompanyId) external onlyRole(Role.Logistics) {
        Batch storage b = batches[batchId];

        require(bytes(b.batchId).length > 0, "Batch not found");
        require(b.status == Status.Ordered, "Batch not ready for collection");

        b.logistics = msg.sender;
        b.logisticsCompanyId = logisticsCompanyId;
        b.status = Status.Collected;

        _logTransition(batchId, "Batch Collected", string(
            abi.encodePacked("Logistics company ", logisticsCompanyId, " collected the batch")
        ), Role.Logistics);
    }

    function confirmDelivery(string memory batchId) external onlyRole(Role.Logistics) {
        Batch storage b = batches[batchId];

        require(bytes(b.batchId).length > 0, "Batch not found");
        require(b.status == Status.Collected, "Batch must be collected first");
        require(b.logistics == msg.sender, "You are not the assigned logistics");

        b.status = Status.Delivered;

        _logTransition(
            batchId,
            "Batch Delivered",
            string(abi.encodePacked("Logistics company ", b.logisticsCompanyId, " delivered the batch")),
            Role.Logistics
        );
    }

    // ---------------- Internal Logging ----------------

    function _logFarmTransition(string memory farmId, string memory action, string memory description, Role role) internal {
        farmHistory[farmId].push(Transition({
            actor: msg.sender,
            role: role,
            action: action,
            description: description,
            timestamp: block.timestamp
        }));

        emit TransitionLogged(farmId, msg.sender, role, action, description, block.timestamp); // You can add a new event if preferred
    }

    function _logTransition(string memory batchId, string memory action, string memory description, Role role) internal {
        batchHistory[batchId].push(Transition({
            actor: msg.sender,
            role: role,
            action: action,
            description: description,
            timestamp: block.timestamp
        }));

        emit TransitionLogged(batchId, msg.sender, role, action, description, block.timestamp);
    }

    function _logAgencyTransition(string memory agencyId, string memory action, string memory description, Role role) internal {
        agencyHistory[agencyId].push(Transition({
            actor: msg.sender,
            role: role,
            action: action,
            description: description,
            timestamp: block.timestamp
        }));

        emit TransitionLogged(agencyId, msg.sender, role, action, description, block.timestamp);
    }

    function _logLogisticsTransition(string memory companyId, string memory action, string memory description, Role role) internal {
        logisticsHistory[companyId].push(Transition({
            actor: msg.sender,
            role: role,
            action: action,
            description: description,
            timestamp: block.timestamp
        }));
    }


    function _roleToString(Role r) internal pure returns (string memory) {
        if (r == Role.Admin) return "Admin";
        if (r == Role.Farmer) return "Farmer";
        if (r == Role.Trader) return "Trader";
        if (r == Role.Logistics) return "Logistics";
        return "Unknown";
    }

    // ---------------- View Functions ----------------
    function getFarmMilestone(string memory farmId) external view returns (
        string[] memory actions,
        string[] memory descriptions,
        address[] memory actors,
        string[] memory roleNames,
        uint256[] memory timestamps
    ) {
        Transition[] storage logs = farmHistory[farmId];
        uint256 len = logs.length;

        require(len > 0, "No milestone history found for this farm");

        actions = new string[](len);
        descriptions = new string[](len);
        actors = new address[](len);
        roleNames = new string[](len);
        timestamps = new uint256[](len);

        for (uint i = 0; i < len; i++) {
            actions[i] = logs[i].action;
            descriptions[i] = logs[i].description;
            actors[i] = logs[i].actor;
            roleNames[i] = _roleToString(logs[i].role);
            timestamps[i] = logs[i].timestamp;
        }
    }


    function getBatchMilestone(string memory batchId) external view returns (
        string[] memory actions,
        string[] memory descriptions,
        address[] memory actors,
        string[] memory roleNames,
        uint256[] memory timestamps
    ) {
        Transition[] storage logs = batchHistory[batchId];
        uint256 len = logs.length;

        require(len > 0, "No milestone history found for this batch");

        actions = new string[](len);
        descriptions = new string[](len);
        actors = new address[](len);
        roleNames = new string[](len);
        timestamps = new uint256[](len);

        for (uint i = 0; i < len; i++) {
            actions[i] = logs[i].action;
            descriptions[i] = logs[i].description;
            actors[i] = logs[i].actor;
            roleNames[i] = _roleToString(logs[i].role);
            timestamps[i] = logs[i].timestamp;
        }
    }

    function getTraderAgencyMilestone(string memory agencyId) external view returns (
        string[] memory actions,
        string[] memory descriptions,
        address[] memory actors,
        string[] memory roleNames,
        uint256[] memory timestamps
    ) {
        Transition[] storage logs = agencyHistory[agencyId];
        uint256 len = logs.length;

        require(len > 0, "No milestone history found for this agency");

        actions = new string[](len);
        descriptions = new string[](len);
        actors = new address[](len);
        roleNames = new string[](len);
        timestamps = new uint256[](len);

        for (uint i = 0; i < len; i++) {
            actions[i] = logs[i].action;
            descriptions[i] = logs[i].description;
            actors[i] = logs[i].actor;
            roleNames[i] = _roleToString(logs[i].role);
            timestamps[i] = logs[i].timestamp;
        }
    }

    function getLogisticsMilestone(string memory companyId) external view returns (
        string[] memory actions,
        string[] memory descriptions,
        address[] memory actors,
        string[] memory roleNames,
        uint256[] memory timestamps
    ) {
        Transition[] storage logs = logisticsHistory[companyId];
        uint256 len = logs.length;

        require(len > 0, "No milestone history found for this logistics company");

        actions = new string[](len);
        descriptions = new string[](len);
        actors = new address[](len);
        roleNames = new string[](len);
        timestamps = new uint256[](len);

        for (uint i = 0; i < len; i++) {
            actions[i] = logs[i].action;
            descriptions[i] = logs[i].description;
            actors[i] = logs[i].actor;
            roleNames[i] = _roleToString(logs[i].role);
            timestamps[i] = logs[i].timestamp;
        }
    }


    function getBatchById(string memory batchId) external view returns (
        string memory,
        string memory,
        string memory,
        uint256,
        string memory,
        Status,
        address,
        address,
        address,
        string memory,
        string memory
    ) {
        Batch storage b = batches[batchId];
        require(bytes(b.batchId).length > 0, "Batch not found");

        return (
            b.batchId,
            b.variety,
            b.farmId,
            b.quantity,
            b.farmLocation,
            b.status,
            b.farmer,
            b.trader,
            b.logistics,
            b.batchImageCID,
            b.deliveryDestination
        );
    }

    function getDuriansByBatchId(string memory batchId) external view returns (Durian[] memory) {
        require(batches[batchId].durians.length > 0, "No durians found in this batch");
        return batches[batchId].durians;
    }


    function getTraderAgencyById(string memory agencyId) external view returns (
        string memory,
        string memory,
        address,
        string memory,
        uint256
    ) {
        TraderAgency storage agency = traderAgencies[agencyId];
        require(bytes(agency.agencyId).length > 0, "Agency not found");

        return (
            agency.agencyId,
            agency.agencyName,
            agency.owner,
            agency.exportLicenseCID,
            agency.exportLicenseExpiry
        );
    }

    function getFarmById(string memory farmId) external view returns (
        string memory,
        string memory,
        address,
        string memory,
        uint256
    ) {
        Farm storage f = farms[farmId];
        require(bytes(f.farmId).length > 0, "Farm not found");

        return (
            f.farmId,
            f.location,
            f.owner,
            f.certificateCID,
            f.certificateExpiry
        );
    }

    function getLogisticsById(string memory companyId) external view returns (
        string memory,
        address,
        string memory
    ) {
        LogisticsCompany storage company = logisticsCompanies[companyId];
        require(bytes(company.companyId).length > 0, "Logistics company not found");

        return (
            company.companyId,
            company.owner,
            company.companyName
        );
    }

    function _uintToString(uint v) internal pure returns (string memory) {
        if (v == 0) return "0";
        uint digits;
        uint temp = v;
        while (temp != 0) {
            digits++;
            temp /= 10;
        }
        bytes memory buffer = new bytes(digits);
        while (v != 0) {
            digits -= 1;
            buffer[digits] = bytes1(uint8(48 + v % 10));
            v /= 10;
        }
        return string(buffer);
    }

}
