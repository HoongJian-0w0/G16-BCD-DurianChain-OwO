import request from '@/utils/request';
import type { AgencyModel } from '@/api/trader/agency/AgencyModels';

/**
 * Get all trader agencies
 */
export function getAllAgencies() {
    return request.get('/trader-agency');
}

/**
 * Get a trader agency by ID
 */
export function fetchAgencyById(id: number) {
    return request.get(`/trader-agency/${id}`);
}

/**
 * Add a new trader agency
 */
export function createAgency(data: AgencyModel) {
    return request.post('/trader-agency', data);
}

/**
 * Update an existing trader agency
 */
export function updateAgency(data: AgencyModel) {
    return request.put('/trader-agency', data);
}

/**
 * Delete a trader agency by ID
 */
export function deleteAgency(id: number) {
    return request.delete(`/trader-agency/${id}`);
}

/**
 * Batch delete trader agencies
 */
export function deleteAgenciesBatch(ids: number[]) {
    return request.delete(`/trader-agency/del/batch/${ids.join(',')}`);
}

/**
 * Get paginated trader agencies
 */
export const getAgencyPage = (params: {
    pageNum: number;
    pageSize: number;
    agencyId?: string;
    agencyName?: string;
    ownerAddress?: string;
}) => {
    return request.get('/trader-agency/page', { params });
};
