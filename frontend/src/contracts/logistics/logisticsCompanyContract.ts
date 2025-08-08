import { getContract } from '@/contracts/web3';

export async function registerLogisticsCompany(
    companyId: string,
    companyName: string,
): Promise<string> {
    const contract = await getContract();
    const tx = await contract.registerLogisticsCompany(companyId, companyName, location);
    await tx.wait();
    return tx.hash;
}

export async function getLogisticsCompanyById(companyId: string): Promise<{
    companyId: string;
    owner: string;
    companyName: string;
}> {
    const contract = await getContract();
    const [id, owner, name] = await contract.getLogisticsById(companyId);
    return {
        companyId: id,
        owner,
        companyName: name,
    };
}

export async function getMyLogisticsCompanyIds(): Promise<string[]> {
    const contract = await getContract();
    return await contract.getMyLogisticsCompanyIds(); // mapping(address => string[])
}
