import { getContract } from '@/utils/web3';

export async function registerFarm(farmId: string, location: string, certificateCID: string, certificateExpiry: number) {
    const contract = await getContract();
    const tx = await contract.registerFarm(farmId, location, certificateCID, certificateExpiry);
    await tx.wait();
    return tx.hash;
}

export async function updateFarmCertificate(farmId: string, newCID: string, newExpiry: number) {
    const contract = await getContract();
    const tx = await contract.updateFarmCertificate(farmId, newCID, newExpiry);
    await tx.wait();
    return tx.hash;
}

export async function getMyFarmIds(): Promise<string[]> {
    const contract = await getContract();
    return await contract.getMyFarmIds();
}

export async function getFarmById(id: string): Promise<{
    farmId: string;
    owner: string;
    location: string;
    certificateCID: string;
    certificateExpiry: string;
}> {
    const contract = await getContract();
    const result = await contract.getFarmById(id);
    return {
        farmId: result[0],
        location: result[1],
        owner: result[2],
        certificateCID: result[3],
        certificateExpiry: result[4].toString(),
    };
}

export async function getFarmMilestone(farmId: string): Promise<
    {
        action: string;
        description: string;
        actor: string;
        role: string;
        timestamp: string;
    }[]
> {
    const contract = await getContract();
    const [actions, descriptions, actors, roleNames, timestamps] = await contract.getFarmMilestone(farmId);

    return actions.map((_, i) => ({
        action: actions[i],
        description: descriptions[i],
        actor: actors[i],
        role: roleNames[i],
        timestamp: timestamps[i].toString(),
    }));
}
