// src/models/authModels.ts
import type { FormRules } from 'element-plus'

export interface LoginDTO {
    username: string
    password: string
    wallet: string
}

export interface RegisterDTO {
    username: string
    password: string
    email: string
    phone: string
    name: string
    role: string
    walletAddress: string
}

export interface LoginVO {
    token: string
    id: number
    username: string
    name: string
    role: string
    wallet: string
}

export const loginRules: FormRules = {
    username: [
        { required: true, message: 'Please enter username', trigger: 'blur' }
    ],
    password: [
        { required: true, message: 'Please enter password', trigger: 'blur' }
    ],
    wallet: [
        { required: true, message: 'Please connect your wallet', trigger: 'blur' }
    ]
}

export const registerRules: FormRules = {
    username: [
        { required: true, message: 'Please enter username', trigger: 'blur' }
    ],
    password: [
        { required: true, message: 'Please enter password', trigger: 'blur' },
        { min: 6, message: 'Password must be at least 6 characters', trigger: 'blur' }
    ],
    email: [
        { required: true, message: 'Please enter email', trigger: 'blur' },
        { type: 'email', message: 'Invalid email format', trigger: ['blur', 'change'] }
    ],
    phone: [
        { required: true, message: 'Please enter phone number', trigger: 'blur' },
        { pattern: /^[0-9]{10,15}$/, message: 'Invalid phone number', trigger: 'blur' }
    ],
    name: [
        { required: true, message: 'Please enter full name', trigger: 'blur' }
    ],
    role: [
        { required: true, message: 'Please select role', trigger: 'change' }
    ],
    walletAddress: [
        { required: true, message: 'Wallet address is required', trigger: 'blur' },
        {
            pattern: /^0x[a-fA-F0-9]{40}$/,
            message: 'Invalid Ethereum address',
            trigger: 'blur'
        }
    ]
}
