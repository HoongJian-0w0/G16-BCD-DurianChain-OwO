import {FormRules} from "element-plus";

export interface BatchModel {
    id?: number;

    batchId: string; // Unique
    foodName: string;
    farmId?: string;
    quantity: number;

    farmLocation?: string | null;
    batchImageCid?: string | null;
    deliveryDestination?: string | null;

    status: 'Created' | 'Ordered' | 'Collected' | 'Delivered' | 'Deleted';

    farmerAddress?: string | null;
    traderAddress?: string | null;
    traderAgencyId?: string | null;

    logisticsCompanyId?: string | null;
    logisticsAddress?: string | null;

    txHash?: string | null;

    durianIds?: string[];
    imageHashes?: string[];

    durians?: {
        durianId: string;
        imageUrl: string;
        imageHash: string;
    }[];
}


export interface BatchQueryParams {
    pageNum: number;
    pageSize: number;
    batchId?: string;
    farmId?: string;
    foodName?: string;
    status?: string;
    statuses?: string[];
    farmerAddress?: string;
    traderAddress?: string;
    traderNullOrMe?: boolean;
    logisticsAddress?: string;
    logisticsNullOrMe?: boolean;
    traderAgencyId?: string;
    logisticsCompanyId?: string;
    onChain?: boolean;
}
export const batchRules: FormRules = {
    batchId: [
        { required: true, message: 'Batch ID is required', trigger: 'blur' },
        { min: 3, max: 100, message: 'Batch ID must be between 3 and 100 characters', trigger: 'blur' }
    ],
    foodName: [
        { required: true, message: 'Food name is required', trigger: 'blur' },
        { min: 2, max: 255, message: 'Food name must be between 2 and 255 characters', trigger: 'blur' }
    ],
    farmId: [
        { required: true, message: 'Farm ID is required', trigger: 'change' }
    ],
    quantity: [
        { required: true, message: 'Quantity is required', trigger: 'blur' },
        { type: 'number', min: 1, message: 'Quantity must be at least 1', trigger: 'blur' }
    ],
    status: [
        { required: true, message: 'Status is required', trigger: 'change' }
    ],
    batchImageCid: [
        { required: true, message: 'Batch image is required (upload an image)', trigger: 'blur' },
        { max: 100, message: 'CID cannot exceed 100 characters', trigger: 'blur' }
    ],
    txHash: [
        { pattern: /^0x([A-Fa-f0-9]{64})$/, message: 'Invalid transaction hash', trigger: 'blur' }
    ]
};