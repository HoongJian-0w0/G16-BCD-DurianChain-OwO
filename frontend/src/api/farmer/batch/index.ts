import request from '@/utils/request';
import type { BatchModel, BatchQueryParams } from '@/api/farmer/batch/BatchModels';

export interface BatchOnChainDTO {
    batchId: string;
    variety: string;
    quantity: number;
    farmId: string;
    batchImageCID: string;
    durianIds: string[];
    imageHashes: string[];
    farmerAddress: string;
}

const BASE_URL = '/batch';

export function getAllBatches() {
    return request.get(`${BASE_URL}`);
}

export function getBatchById(id: number) {
    return request.get(`${BASE_URL}/${id}`);
}

export function createBatch(data: BatchModel) {
    return request.post(BASE_URL, data);
}

export function updateBatch(data: BatchModel) {
    return request.put(BASE_URL, data);
}

export function deleteBatch(id: number) {
    return request.delete(`${BASE_URL}/${id}`);
}

export function deleteBatchByIds(ids: number[]) {
    return request.delete(`${BASE_URL}/del/batch/${ids.join(',')}`);
}

export function getBatchPage(params: BatchQueryParams) {
    return request.get(`${BASE_URL}/page`, { params });
}
/**
 * Save on-chain batch metadata to backend database
 */
export function saveBatchOffChain(data: BatchOnChainDTO) {
    return request.post('/batch/off-chain', data);
}

export function getBatchesByStatus(params: {
    status?: string;
    statuses?: string[];
    farmerAddress?: string;
    traderAddress?: string;
    logisticsAddress?: string;
}) {
    return request.get(`${BASE_URL}/by-status`, { params });
}

export function getTxHashByBatchId(batchId: string) {
    return request.get(`${BASE_URL}/txhash/${batchId}`);
}