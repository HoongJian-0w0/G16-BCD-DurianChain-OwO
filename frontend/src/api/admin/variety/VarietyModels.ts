export interface VarietyModel {
    id?: number;
    varietyId: string;
    name: string;
    description?: string;
    originRegion?: string;
}

export const defaultVarietyForm: VarietyModel = {
    varietyId: '',
    name: '',
    description: '',
    originRegion: ''
};

export const varietyRules = {
    varietyId: [
        { required: true, message: 'Please enter Variety ID', trigger: 'blur' }
    ],
    name: [
        { required: true, message: 'Please enter Name', trigger: 'blur' }
    ],
    originRegion: [
        { required: true, message: 'Please enter Name', trigger: 'blur' }
    ]
};
