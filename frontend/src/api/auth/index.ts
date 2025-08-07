import request from '@/utils/request'
import type { LoginDTO, RegisterDTO, LoginVO } from '@/api/auth/AuthModels'

/**
 * Register a new user
 */
export function register(data: RegisterDTO) {
    return request.post('/auth/register', data)
}

/**
 * Login and receive user token + profile
 */
export function login(data: LoginDTO) {
    return request.post('/auth/login', data)
}
