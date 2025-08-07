import { FormRules } from "element-plus";

export interface LogisticsCompany {
    id?: number;
    companyId: string;
    companyName: string;
    ownerAddress: string;
    txHash: string;
}

export const logisticsCompanyRules: FormRules = {
    companyId: [
        { required: true, message: 'Please enter Company ID', trigger: 'blur' },
        { min: 5, max: 100, message: 'Length should be between 5 and 100 characters', trigger: 'blur' }
    ],
    companyName: [
        { required: true, message: 'Please enter Company Name', trigger: 'blur' },
        { min: 3, message: 'Company Name must be at least 3 characters', trigger: 'blur' }
    ],
    ownerAddress: [
        { required: true, message: 'Please enter wallet address', trigger: 'blur' },
        { pattern: /^0x[a-fA-F0-9]{40}$/, message: 'Invalid Ethereum address format', trigger: 'blur' }
    ]
};
