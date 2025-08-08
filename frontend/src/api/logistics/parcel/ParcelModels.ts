// /api/logistics/parcel/ParcelModels.ts

export interface ParcelDTO {
    batchId: string;               // ID of the durian batch
    logisticsCompanyId: string;    // ID of the logistics company
    logisticsAddress: string;      // Wallet address of the logistics user
    txHash: string;                // On-chain transaction hash
}