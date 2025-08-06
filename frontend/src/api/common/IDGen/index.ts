import request from '@/utils/request'

/**
 * Generate an ID by type
 * @param type The type of entity: 'farm', 'batch', 'durian', 'agency', etc.
 * @returns Promise resolving to the generated ID string
 */
export function generateId(type: string) {
    return request.get(`/util/genId`, {
        params: { type }
    });
}