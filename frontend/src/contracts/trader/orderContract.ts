import { getContract } from '@/contracts/web3';

/**
 * Trader orders a batch (local or export)
 * @param batchId - ID of the batch
 * @param agencyId - ID of the trader agency
 * @param deliveryDestination - Destination address
 * @param isExport - Whether it’s an export order
 */
export async function orderBatch(
    batchId: string,
    agencyId: string,
    deliveryDestination: string,
    isExport: boolean
): Promise<string> {
    const contract = await getContract();
    const tx = await contract.orderBatch(batchId, agencyId, deliveryDestination, isExport);
    await tx.wait();
    return tx.hash;
}

/**
 * Trader cancels an existing order (only if status is Ordered)
 * @param batchId - ID of the batch to cancel
 */
export async function cancelOrder(batchId: string): Promise<string> {
    const contract = await getContract();
    const tx = await contract.cancelOrder(batchId);
    await tx.wait();
    return tx.hash;
}
