export interface AgencyModel {
    id?: number;
    agencyId: string;
    ownerAddress: string;
    agencyName: string;
    exportLicenseCid?: string;
    exportLicenseExpiry?: string;
    txHash?: string;
}

export const agencyRules = {
    agencyId: [
        { required: true, message: 'Agency ID is required', trigger: 'blur' },
        { min: 3, max: 100, message: 'Agency ID must be between 3 to 100 characters', trigger: 'blur' },
    ],
    ownerAddress: [
        { required: true, message: 'Owner wallet address is required', trigger: 'blur' },
        { pattern: /^0x[a-fA-F0-9]{40}$/, message: 'Invalid Ethereum address', trigger: 'blur' },
    ],
    agencyName: [
        { required: true, message: 'Agency name is required', trigger: 'blur' },
        { min: 2, max: 255, message: 'Agency name must be between 2 to 255 characters', trigger: 'blur' },
    ],
    exportLicenseCid: [
        {
            required: false,
            pattern: /^[a-zA-Z0-9]+$/, // Simple pattern (optional field)
            message: 'Export CID must be alphanumeric',
            trigger: 'blur',
        },
    ],
    exportLicenseExpiry: [
        {
            required: false,
            type: 'date',
            message: 'Please select a valid expiry date',
            trigger: 'change',
        },
    ],
};