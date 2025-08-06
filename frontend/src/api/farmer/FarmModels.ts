import {FormRules} from "element-plus";

export interface FarmModel {
    id?: number;
    farmId: string;
    ownerAddress: string;
    location: string;
    latitude: string;
    longitude: string;
    certificateCid: string;
    certificateExpiry: string;
    txHash: string;
}

export const farmRules: FormRules = {
    /*farmId: [
        { required: true, message: 'Farm ID is required', trigger: 'blur' },
        { min: 3, max: 100, message: 'Farm ID must be between 3 and 100 characters', trigger: 'blur' }
    ],
    ownerAddress: [
        { required: true, message: 'Owner wallet address is required', trigger: 'blur' },
        { min: 42, max: 66, message: 'Invalid wallet address length', trigger: 'blur' }
    ],
    location: [
        { required: true, message: 'Location is required', trigger: 'blur' }
    ],
    latitude: [
        { required: true, message: 'Latitude is required', trigger: 'blur' },
        { validator: (_rule, value, callback) => {
                if (isNaN(Number(value))) return callback(new Error('Latitude must be a number'));
                callback();
            }, trigger: 'blur'
        }
    ],
    longitude: [
        { required: true, message: 'Longitude is required', trigger: 'blur' },
        { validator: (_rule, value, callback) => {
                if (isNaN(Number(value))) return callback(new Error('Longitude must be a number'));
                callback();
            }, trigger: 'blur'
        }
    ],
    certificateCid: [
        {
            validator: (_rule, value, callback, source) => {
                if (value && !source.certificateExpiry) {
                    return callback(new Error('Expiry date is required when certificate is provided'));
                }
                callback();
            },
            trigger: 'blur'
        }
    ],
    certificateExpiry: [
        {
            validator: (_rule, value, callback, source) => {
                if (value && !source.certificateCid) {
                    return callback(new Error('Certificate is required when expiry date is provided'));
                }
                callback();
            },
            trigger: 'change'
        }
    ]*/
};