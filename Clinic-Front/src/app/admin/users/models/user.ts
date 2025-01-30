import {Role} from "./role";

export interface User {
  id?: number;
  name: string;
  email: string;
  password: string;
  dob?: Date | null;
  role?: Role;
  accountLocked?: boolean;
  verified?: boolean;
}
