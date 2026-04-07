export interface PasswordPolicy {
  minLength: number;
  maxLength: number;
  requireUppercase: boolean;
  requireLowercase: boolean;
  requireNumber: boolean;
  requireSpecial: boolean;
  passwordExpiryDays: number;
  preventReuse: number;
  maxAttempts: number;
  lockoutDuration: number;
}

export interface ValidationRuleResult {
  rule: string;
  isValid: boolean;
  message: string;
}

export interface ValidationResult {
  isValid: boolean;
  errors: string[];
  failedRequirements: string[];
  passedRequirements: string[];
}

export interface RequirementCheck {
  name: string;
  passed: boolean;
  message: string;
}
