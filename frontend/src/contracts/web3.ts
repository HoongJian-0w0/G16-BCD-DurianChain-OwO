import { ethers } from "ethers";
import DurianChainABI from "@/contracts/DurianChain.json";

const CONTRACT_ADDRESS = "0xB82008565FdC7e44609fA118A4a681E92581e680";

export async function getContract() {
    if (!window.ethereum) throw new Error("MetaMask not found");

    await window.ethereum.request({ method: "eth_requestAccounts" });
    const provider = new ethers.BrowserProvider(window.ethereum);
    const signer = await provider.getSigner();
    const contract = new ethers.Contract(CONTRACT_ADDRESS, DurianChainABI.abi, signer);

    return contract;
}
