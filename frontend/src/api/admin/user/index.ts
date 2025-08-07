import request from '@/utils/request';
import type { UserModel } from './UserModels';

// Fetch paginated user list (with optional filters)
export function getUserPage(params: {
    pageNum: number;
    pageSize: number;
    username?: string;
    email?: string;
}) {
    return request.get('/user/page', { params });
}

// Create a new user
export function createUser(data: UserModel) {
    return request.post('/user', data);
}

// Update existing user
export function updateUser(data: UserModel) {
    return request.put('/user', data);
}
