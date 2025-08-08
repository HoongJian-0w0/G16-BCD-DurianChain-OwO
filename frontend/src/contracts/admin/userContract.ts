import { getContract } from '@/contracts/web3';

export enum Role {
    Unknown = 0,
    Admin = 1,
    Farmer = 2,
    Logistics = 3,
    Trader = 4
}

export async function assignUserRole(user: string, role: Role): Promise<string> {
    const contract = await getContract();
    const tx = await contract.assignRole(user, role);
    await tx.wait();
    return tx.hash;
}

export async function getUserRole(user: string): Promise<Role> {
    const contract = await getContract();
    return await contract.getUserRole(user); // returns enum index (0–4)
}

export async function getMyRoleAsString(): Promise<string> {
    const contract = await getContract();
    return await contract.getMyRoleAsString(); // returns string e.g. "admin"
}
