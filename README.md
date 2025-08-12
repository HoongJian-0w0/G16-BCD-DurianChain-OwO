# DurianChain – Blockchain-Powered Durian Traceability System

<p align="center">
  <img src="system-preview/DurianChain-WebLogo-01.svg" alt="DurianChain Logo" width="250"/>
</p>

## Introduction
DurianChain is a hybrid Web2/Web3 decentralized application (DApp) designed to combat fraudulent practices in Malaysia’s durian industry, particularly the mislabeling of cheaper varieties such as D24 as the premium Musang King. By leveraging blockchain technology, DurianChain ensures end-to-end transparency, authenticity, and accountability in the supply chain — from farm to consumer.

This system integrates role-based access control, QR code product verification, and immutable blockchain records to provide a tamper-proof history of each durian batch. Consumers, exporters, regulators, and retailers can all verify the origin, quality, and handling of durians through a simple QR code scan, strengthening trust in Malaysia’s premium durian exports.

## Quick Description
- **Industry:** Food & Beverage – Malaysian Durian Sector
- **Core Problem:** Fraudulent mislabeling, counterfeit branding, and unverifiable claims of origin.
- **Solution:** Hybrid blockchain-based traceability platform with smart contracts, secure data recording, and QR-based verification for batch-level traceability.
- **Key Roles:** Admin, Farmer, Logistics Provider, Retailer/Exporter (Trader), Consumer.
- **Outcome:** Enhances consumer trust, protects honest producers, streamlines export compliance, and boosts Malaysia’s global durian brand.

## Technologies Utilized
- **Backend:** [Spring Boot](https://spring.io/projects/spring-boot) – REST API server for core business logic and integration with blockchain.
- **Database:** [MySQL](https://www.mysql.com/) – Relational database for storing off-chain data such as user profiles, farm details, and product metadata.
- **Frontend:** [Vue.js](https://vuejs.org/) – Responsive web interface with role-based access for farmers, logistics providers, traders, and consumers.
- **Blockchain:** [Hardhat](https://hardhat.org/) + [Solidity](https://soliditylang.org/) – Smart contract development and local blockchain deployment for batch traceability and ownership transfer.

## File Structure
<div align="left">
/Root <br>
├── backend # Spring Boot backend project (REST API, database integration) <br/>
├── frontend # Vue.js frontend application (UI, QR scanning, role-based dashboard)  <br/>
└── blockchain # Hardhat project with Solidity smart contracts for traceability
</div>

## Installation Guidelines
### Backend (Spring Boot)
1. **Install MySQL**
    - Ensure MySQL **version 5.0 or higher** is installed and running.
    - Create a database named `durianchain`.

2. **Import Database Schema**
    - Locate the DDL file at `/backend/durianchain.sql`.
    - Import it into your MySQL instance using a tool such as **MySQL Workbench** or the CLI:
      ```bash
      mysql -u root -p durianchain < backend/durianchain.sql
      ```

3. **Configure Database Connection**
    - Open `/backend/src/main/resources/application.yml`.
    - Update the database URL, username, and password according to your local MySQL credentials:
      ```yaml
      url: jdbc:mysql://localhost:3306/durianchain?serverTimezone=UTC
      username: root
      password: 1234
      ```
      > Replace `root` and `1234` with your **own MySQL username and password**.

4. **Configure Environment Variables**
   - In the `/backend` directory, create a file named `.env`.
   - Add your **own Pinata API credentials** as follows:
     ```env
     PINATA_JWT=Bearer <your-own-pinata-jwt-token>
     PINATA_BASE_URL=https://api.pinata.cloud
     ```
     > **Important:** Replace `<your-own-pinata-jwt-token>` with your own Pinata JWT from your Pinata account.  
     > Do **NOT** commit your `.env` file to Git — keep it private.

   - In `/backend/src/main/resources/application.yml`, ensure the `pinata` configuration uses the environment variables:
     ```yaml
     pinata:
       jwt: ${PINATA_JWT}
       base-url: ${PINATA_BASE_URL}
     ```
     
5. **Run the Backend**
   - Open the project in **IntelliJ IDEA**.
   - In the project explorer, navigate to:
     ```
     /backend/src/main/java/com/durianchain/BackendApplication.java
     ```
   - Right-click on `BackendApplication.java` and select **Run 'BackendApplication'**.
   - The backend API should now be available at:  
     **http://localhost:9090**

### Frontend (Vue.js)
1. **Navigate to the Frontend Folder**  
   From the root directory, run:
   ```bash
   cd frontend

2. **Install Dependencies**
   ```bash
   npm install
   ```

3. **Configure Environment Variables**
   - Create a .env file in /frontend.
   - Add:
     ```env
     VITE_CLOUDINARY_CLOUD_NAME=<your-cloudinary-cloud-name>
     VITE_CLOUDINARY_UPLOAD_PRESET=<your-cloudinary-upload-preset>
     ```
     > **Important:** Replace <your-cloudinary-cloud-name> and <your-cloudinary-upload-preset> with the values from your own Cloudinary account. Keep .env private and do not commit it to Git.

   - In `/backend/src/main/resources/application.yml`, ensure the `pinata` configuration uses the environment variables:
     ```yaml
     pinata:
       jwt: ${PINATA_JWT}
       base-url: ${PINATA_BASE_URL}

4. **Run the Frontend**
     ```bash
     npm run dev
     ```
   - App will be available at: http://localhost:5173

### Blockchain (Hardhat + Solidity)
1. **Navigate to the Blockchain Folder**
   ```bash
   cd blockchain
   ```
2. **Install Dependencies**
   ```bash
   npm install
   ```
   
3. **Configure Environment Variables**
- Create a .env file in /blockchain.
- Add:
  ```env
  ALCHEMY_API_KEY=<your-alchemy-api-key>
  PRIVATE_KEY=<your-wallet-private-key>
  ETHERSCAN_API_KEY=<your-etherscan-api-key>
  ```
  > **Important:** Replace each placeholder with your own values: <your-alchemy-api-key> from your Alchemy account, <your-wallet-private-key> from your wallet (e.g., MetaMask), and <your-etherscan-api-key> from Etherscan (optional, for contract verification). Never commit your .env file to Git — keep it private.

4. **Compile Smart Contracts**
   ```bash
   npx hardhat compile
   ```

5. **Start a Local Blockchain Node**
   ```bash
   npx hardhat node
   ```

6. **Deploy Contracts to Localhost**
   Open a new terminal tab/window (keep the node running) and run:
   ```bash
   npx hardhat run scripts/deploy.js --network localhost
   ```
   - Copy the **deployed contract address** from the console output.
   - If your apps need it, set it in the frontend and/or backend:
      - Frontend `.env`:
        ```env
        VITE_CONTRACT_ADDRESS=<deployed-contract-address>
        ```
      - Backend `.env` (if applicable):
        ```env
        CONTRACT_ADDRESS=<deployed-contract-address>
        ```

7. **(Optional) Deploy to Testnet/Mainnet**
   Ensure your `.env` is configured and you have test funds in the account for the selected network.
   ```bash
   npx hardhat run scripts/deploy.js --network sepolia
   ```
   - Replace `sepolia` with any configured network.

8. **(Optional) Verify on Etherscan**
   After a successful testnet/mainnet deploy, verify the contract:
   ```bash
   npx hardhat verify --network sepolia <deployed-contract-address> <constructor-arg-1> <constructor-arg-2>
   ```
   - Omit constructor args if your constructor takes none.

9. **Example `hardhat.config.js` (env-based)**
   ```js
   require("@nomicfoundation/hardhat-toolbox");
   require("dotenv").config();

   const { ALCHEMY_API_KEY, PRIVATE_KEY, ETHERSCAN_API_KEY } = process.env;

   module.exports = {
     solidity: "0.8.24",
     networks: {
       localhost: { url: "http://127.0.0.1:8545" },
       sepolia: {
         url: `https://eth-sepolia.g.alchemy.com/v2/${ALCHEMY_API_KEY}`,
         accounts: PRIVATE_KEY ? [PRIVATE_KEY] : [],
       },
       // add more networks as needed
     },
     etherscan: {
       apiKey: ETHERSCAN_API_KEY || "",
     },
   };
   ```

### Linking Blockchain Deployment to Frontend
After deploying your smart contract with Hardhat, you need to update the frontend with the **deployed contract address** and **ABI**.

1. **Copy the ABI File**
   - Locate the generated ABI file after deployment:
     ```
     blockchain/artifacts/contracts/DurianChain.sol/DurianChain.json
     ```
   - Copy this file into the frontend:
     ```
     frontend/src/contracts/DurianChain.json
     ```

2. **Update the Contract Address**
   - Open:
     ```
     frontend/src/contracts/web3.ts
     ```
   - Replace the placeholder in `CONTRACT_ADDRESS` with your deployed contract address:
     ```ts
     const CONTRACT_ADDRESS = "<your-deployed-contract-address>";
     ```

3. **Example `web3.ts`**
   ```ts
   import { ethers } from "ethers";
   import DurianChainABI from "@/contracts/DurianChain.json";

   // Replace with deployed address from Hardhat console
   const CONTRACT_ADDRESS = "<your-deployed-contract-address>";

   export async function getContract() {
       if (!window.ethereum) throw new Error("MetaMask not found");

       await window.ethereum.request({ method: "eth_requestAccounts" });
       const provider = new ethers.BrowserProvider(window.ethereum);
       const signer = await provider.getSigner();
       const contract = new ethers.Contract(CONTRACT_ADDRESS, DurianChainABI.abi, signer);

       return contract;
   }
   
