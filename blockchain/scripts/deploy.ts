import { ethers } from "hardhat";

async function main() {
    const DurianChain = await ethers.getContractFactory("DurianChain");
    const contract = await DurianChain.deploy();

    await contract.waitForDeployment();

    const address = await contract.getAddress();
    console.log("DurianChain deployed to:", address);
}

main().catch((error) => {
    console.error(error);
    process.exitCode = 1;
});
