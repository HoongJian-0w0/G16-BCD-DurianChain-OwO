import request from '@/utils/request';
import type { Durian, DurianQueryParams } from '@/api/farmer/durian/DurianModels';

/**
 * Get all durians (no pagination)
 */
export function getAllDurians() {
    return request.get('/durian');
}

/**
 * Get paginated and filtered durian list
 */
export function getDurianPage(params: DurianQueryParams) {
    return request.get('/durian/page', { params });
}

/**
 * Get a durian by ID
 */
export function getDurianById(id: number) {
    return request.get(`/durian/${id}`);
}

/**
 * Create a new durian
 */
export function createDurian(data: Durian) {
    return request.post('/durian', data);
}

/**
 * Update an existing durian
 */
export function updateDurian(data: Durian) {
    return request.put('/durian', data);
}

/**
 * Delete a durian by ID
 */
export function deleteDurian(id: number) {
    return request.delete(`/durian/${id}`);
}

/**
 * Batch delete durians by ID list
 */
export function deleteDuriansBatch(ids: number[]) {
    return request.delete(`/durian/del/batch/${ids.join(',')}`);
}
