import { HardhatUserConfig } from "hardhat/config";
import "@nomicfoundation/hardhat-toolbox";

const config: HardhatUserConfig = {
  solidity: {
    version: "0.8.19",
    settings: {
      optimizer: {
        enabled: true,
        runs: 50, // You can use 50–200 for deployment optimization
      },
    },
  },
  networks: {
    localhost: {
      url: "http://127.0.0.1:8545"
    }
  }
};

export default config;
