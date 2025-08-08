import { getContract } from '@/contracts/web3';

/**
 * Collect a batch (logistics action)
 * @param batchId - ID of the batch to collect
 * @param logisticsCompanyId - ID of the logistics company collecting the batch
 * @returns transaction hash
 */
export async function collectBatch(batchId: string, logisticsCompanyId: string): Promise<string> {
    const contract = await getContract();
    const tx = await contract.collectBatch(batchId, logisticsCompanyId);
    await tx.wait();
    return tx.hash;
}

/**
 * Confirm delivery (logistics action)
 * @param batchId - ID of the batch being delivered
 * @returns transaction hash
 */
export async function confirmDelivery(batchId: string): Promise<string> {
    const contract = await getContract();
    const tx = await contract.confirmDelivery(batchId);
    await tx.wait();
    return tx.hash;
}
