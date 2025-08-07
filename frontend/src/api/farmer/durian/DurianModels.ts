export interface Durian {
    id?: number;
    durianId: string;
    imageUrl: string;
    imageHash: string;
    farmId: string;
    batchId?: string;
    varietyId?: string;
    walletAddress?: string;
    scanCount?: number;
    onChain?: boolean;
    status?: string;
}

export interface DurianQueryParams {
    pageNum: number;
    pageSize: number;
    durianId?: string;
    farmId?: string;
    batchId?: string;
    varietyId?: string;
    status?: string;
    onChain?: boolean;
    walletAddress?: string;
}

export const durianRules = {
    durianId: [
        { required: true, message: 'Durian ID is required', trigger: 'blur' },
        { min: 3, max: 100, message: 'Durian ID must be 3–100 characters', trigger: 'blur' }
    ],
    imageUrl: [
        { required: true, message: 'Image URL is required', trigger: 'blur' }
    ],
    imageHash: [
        { required: true, message: 'Image Hash is required', trigger: 'blur' },
        { min: 5, message: 'Image Hash seems too short', trigger: 'blur' }
    ],
    farmId: [
        { required: true, message: 'Farm ID is required', trigger: 'blur' }
    ],
    varietyId: [
        { required: true, message: 'Variety is required', trigger: 'change' }
    ]
};
