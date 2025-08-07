import {FormRules} from "element-plus";

export interface FarmModel {
    id?: number;
    farmId: string;
    ownerAddress: string;
    location: string;
    latitude: string;
    longitude: string;
    certificateCid?: string;
    certificateExpiry?: string;
    txHash: string;
}

export const farmRules: FormRules = {
    farmId: [
        { required: true, message: 'Farm ID is required', trigger: 'blur' }
    ],
    ownerAddress: [
        { required: true, message: 'Wallet address is required', trigger: 'blur' },
        {
            validator: (_rule, value, callback) => {
                const valid = /^0x[a-fA-F0-9]{40}$/.test(value);
                callback(valid ? undefined : new Error('Invalid wallet address'));
            },
            trigger: 'blur'
        }
    ],
    location: [{ required: true, message: 'Location is required', trigger: 'blur' }],
    latitude: [
        { required: true, message: 'Latitude is required', trigger: 'blur' },
        {
            validator: (_rule, value, callback) => {
                const num = Number(value);
                if (isNaN(num)) return callback(new Error('Latitude must be a number'));
                if (num < -90 || num > 90) return callback(new Error('Latitude must be between -90 and 90'));
                callback();
            },
            trigger: 'blur'
        }
    ],
    longitude: [
        { required: true, message: 'Longitude is required', trigger: 'blur' },
        {
            validator: (_rule, value, callback) => {
                const num = Number(value);
                if (isNaN(num)) return callback(new Error('Longitude must be a number'));
                if (num < -180 || num > 180) return callback(new Error('Longitude must be between -180 and 180'));
                callback();
            },
            trigger: 'blur'
        }
    ]
};
