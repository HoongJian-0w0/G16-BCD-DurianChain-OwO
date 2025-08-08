import request from '@/utils/request';
import type { Durian, DurianQueryParams } from '@/api/farmer/durian/DurianModels';

export interface DurianIdListDTO {
    durianIds: string[];
}

export interface DurianHashDTO {
    durianId: string;
    imageHash: string;
}

export interface DurianQueryDTO {
    farmId: string;
    varietyId: string;
    status?: string[];
    batchId?: string;
}

export function getAllDurians() {
    return request.get('/durian');
}

export function getDurianPage(params: DurianQueryParams) {
    return request.get('/durian/page', { params });
}

export function getDurianById(id: number) {
    return request.get(`/durian/${id}`);
}

export function createDurian(data: Durian) {
    return request.post('/durian', data);
}

export function updateDurian(data: Durian) {
    return request.put('/durian', data);
}

export function deleteDurian(id: number) {
    return request.delete(`/durian/${id}`);
}

export function deleteDuriansBatch(ids: number[]) {
    return request.delete(`/durian/del/batch/${ids.join(',')}`);
}

export function getDuriansByFarmAndVariety(dto: DurianQueryDTO) {
    const params: Record<string, any> = {
        farmId: dto.farmId,
        varietyId: dto.varietyId,
        ...(dto.status?.length ? { status: dto.status } : {}),
        ...(dto.batchId ? { batchId: dto.batchId } : {})
    };

    return request.get('/durian/by-farm-and-variety', { params });
}

export function getDurianHashesByIds(data: DurianIdListDTO) {
    return request.post('/durian/hash-list', data);
}

export function getDuriansByBatchId(batchId: string) {
    return request.get(`/durian/by-batch/${batchId}`);
}

export async function releaseDurians(batchId: string) {
    return request.put(`/durian/release/${batchId}`);
}