import request from '@/utils/request';
import type { VarietyModel } from '@/api/admin/variety/VarietyModels';

export interface PageParams {
    pageNum: number;
    pageSize: number;
    varietyId?: string;
    name?: string;
    originRegion?: string;
}
// Get paginated varieties
export function getVarietyPage(params: PageParams) {
    return request.get('/durian-variety/page', { params });
}

// Get all varieties (no pagination)
export function getAllVarieties() {
    return request.get('/durian-variety');
}

// Get single variety by ID
export function getVarietyById(id: number) {
    return request.get(`/durian-variety/${id}`);
}

// Create a new variety
export function createVariety(data: VarietyModel) {
    return request.post('/durian-variety', data);
}

// Update existing variety
export function updateVariety(data: VarietyModel) {
    return request.put('/durian-variety', data);
}

// Delete a variety by ID
export function deleteVarietyById(id: number) {
    return request.delete(`/durian-variety/${id}`);
}

// Batch delete by array of IDs
export function deleteVarietiesBatch(ids: number[]) {
    return request.delete(`/durian-variety/del/batch/${ids.join(',')}`);
}
