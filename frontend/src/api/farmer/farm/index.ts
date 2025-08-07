import request from '@/utils/request';
import type { FarmModel } from '@/api/farmer/farm/FarmModels';

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
    owner_address?: string;
    farmId?: string;
    location?: string;
}) => {
    return request.get('/farm/page', { params });
};