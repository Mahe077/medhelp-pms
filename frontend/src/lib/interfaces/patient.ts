//TODO: complete the models
export interface Allergy {
  allergenType: string;
  allergenName: string;
  reaction?: string;
  severity: string;
}

export interface Insurance {
  id: string;
  insurance_type: string;
  policy_number: string;
}

export interface Patient {
  id: string;
  patientNumber: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  phone: string;
  email?: string;
  allergies: Allergy[];
  insurance: Insurance[];
}

export interface CreatePatientRequest {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  phone: string;
  email?: string;
  address: {
    line1: string;
    line2?: string;
    city: string;
    state: string;
    zipCode: string;
  };
  allergies?: Array<{
    allergenType: string;
    allergenName: string;
    reaction?: string;
    severity: string;
  }>;
}
