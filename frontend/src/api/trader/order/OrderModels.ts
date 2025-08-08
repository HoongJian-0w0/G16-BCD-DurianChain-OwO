export interface OrderDTO {
    batchId: string;                // required
    traderAgencyId: string;         // required
    traderAddress: string;          // required (from userStore.walletAddress)
    deliveryDestination?: string;   // optional
    txHash: string;                 // required (from blockchain tx)
}