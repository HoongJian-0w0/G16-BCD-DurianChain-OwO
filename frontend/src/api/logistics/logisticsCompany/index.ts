import request from '@/utils/request'
import type { LogisticsCompany } from '@/api/logistics/logisticsCompany/logisticsCompanyModels'

/**
 * Get all logistics companies
 */
export function getAllLogisticsCompanies() {
    return request.get('/logistics-company')
}

/**
 * Get a logistics company by ID
 */
export function fetchLogisticsCompanyById(id: number) {
    return request.get(`/logistics-company/${id}`)
}

/**
 * Add a new logistics company
 */
export function createLogisticsCompany(data: LogisticsCompany) {
    return request.post('/logistics-company', data)
}

/**
 * Update an existing logistics company
 */
export function updateLogisticsCompany(data: LogisticsCompany) {
    return request.put('/logistics-company', data)
}

/**
 * Delete a logistics company by ID
 */
export function deleteLogisticsCompany(id: number) {
    return request.delete(`/logistics-company/${id}`)
}

/**
 * Batch delete logistics companies by IDs
 */
export function deleteLogisticsCompaniesBatch(ids: number[]) {
    return request.delete(`/logistics-company/del/batch/${ids.join(',')}`)
}

/**
 * Get paginated logistics companies
 */
export function getLogisticsCompanyPage(params: {
    pageNum: number
    pageSize: number
}) {
    return request.get('/logistics-company/page', { params })
}
