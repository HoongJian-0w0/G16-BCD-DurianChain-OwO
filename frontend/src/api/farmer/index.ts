import request from '@/utils/request';
import type { FarmModel } from '@/api/farmer/FarmModels';

interface PageResponse<T> {
    records: T[];
    total: number;
    size: number;
    current: number;
    pages: number;
}

/**
 * Get all farms
 */
export function getAllFarms() {
    return request.get('/farm');
}

/**
 * Get a farm by ID
 */
export function fetchFarmById(id: number) {
    return request.get(`/farm/${id}`);
}

/**
 * Add a new farm
 */
export function createFarm(data: FarmModel) {
    return request.post('/farm', data);
}

/**
 * Update an existing farm
 */
export function updateFarm(data: FarmModel) {
    return request.put('/farm', data);
}

/**
 * Get paginated farms
 */
export const getFarmPage = (params: {
    pageNum: number;
    pageSize: number;
}) => {
    return request.get('/farm/page', { params });
};