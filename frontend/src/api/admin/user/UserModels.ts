export interface UserModel {
    id?: number;
    username: string;
    name: string;
    email: string;
    phone: string;
    role: 'unknown' | 'admin' | 'farmer' | 'logistics' | 'trader';
    walletAddress: string;
    isApproved: boolean;
    createdTime?: string;
    updatedTime?: string;
}

export const userRules = {
    walletAddress: [
        { required: true, message: 'Wallet address is required', trigger: 'blur' },
        {
            pattern: /^0x[a-fA-F0-9]{40}$/,
            message: 'Invalid Ethereum address format',
            trigger: 'blur'
        }
    ],
    username: [
        { required: true, message: 'Username is required', trigger: 'blur' },
        { min: 3, max: 50, message: 'Username must be 3–50 characters', trigger: 'blur' }
    ],
    name: [
        { required: true, message: 'Full name is required', trigger: 'blur' }
    ],
    email: [
        { required: true, message: 'Email is required', trigger: 'blur' },
        { type: 'email', message: 'Email format is invalid', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: 'Phone number is required', trigger: 'blur' },
        { pattern: /^[0-9]{9,15}$/, message: 'Invalid phone number', trigger: 'blur' }
    ],
    role: [
        { required: true, message: 'Role selection is required', trigger: 'change' }
    ]
};
