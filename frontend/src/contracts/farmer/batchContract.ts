import { getContract } from '@/contracts/web3';

export async function createBatch(
    batchId: string,
    variety: string,
    quantity: number,
    farmId: string,
    batchImageCID: string,
    durianIds: string[],
    imageHashes: string[]
): Promise<string> {
    const contract = await getContract();
    const tx = await contract.createBatch(
        batchId,
        variety,
        quantity,
        farmId,
        batchImageCID,
        durianIds,
        imageHashes
    );
    await tx.wait();
    return tx.hash;
}

export async function updateBatchInfo(
    batchId: string,
    newQuantity: number,
    newBatchImageCID: string,
    newDurianIds: string[],
    newImageHashes: string[]
): Promise<string> {
    const contract = await getContract();
    const tx = await contract.updateBatchInfo(
        batchId,
        newQuantity,
        newBatchImageCID,
        newDurianIds,
        newImageHashes
    );
    await tx.wait();
    return tx.hash;
}

export async function removeBatch(batchId: string): Promise<string> {
    const contract = await getContract();
    const tx = await contract.removeBatch(batchId);
    await tx.wait();
    return tx.hash;
}

export async function getMyBatchIds(): Promise<string[]> {
    const contract = await getContract();
    return await contract.getMyBatchIds();
}

export async function getBatchById(batchId: string): Promise<{
    batchId: string;
    variety: string;
    farmId: string;
    quantity: number;
    farmLocation: string;
    status: number;
    farmer: string;
    trader: string;
    logistics: string;
    batchImageCID: string;
    deliveryDestination: string;
}> {
    const contract = await getContract();
    const result = await contract.getBatchById(batchId);
    return {
        batchId: result[0],
        variety: result[1],
        farmId: result[2],
        quantity: Number(result[3]),
        farmLocation: result[4],
        status: Number(result[5]),
        farmer: result[6],
        trader: result[7],
        logistics: result[8],
        batchImageCID: result[9],
        deliveryDestination: result[10],
    };
}

export async function getBatchMilestone(batchId: string): Promise<
    {
        action: string;
        description: string;
        actor: string;
        role: string;
        timestamp: string;
    }[]
> {
    const contract = await getContract();
    const [actions, descriptions, actors, roleNames, timestamps] = await contract.getBatchMilestone(batchId);

    return actions.map((_, i) => ({
        action: actions[i],
        description: descriptions[i],
        actor: actors[i],
        role: roleNames[i],
        timestamp: timestamps[i].toString(),
    }));
}

export async function getDuriansByBatchId(batchId: string): Promise<{
    id: string;
    imageHash: string;
}[]> {
    const contract = await getContract();
    const durians = await contract.getDuriansByBatchId(batchId);
    return durians.map((d: any) => ({
        id: d.id,
        imageHash: d.imageHash
    }));
}