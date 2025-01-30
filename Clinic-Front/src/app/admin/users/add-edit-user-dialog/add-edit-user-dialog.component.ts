import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {User} from "../models/user";
import {Role} from "../models/role";


export interface AddEditUserDialogData {
  user?: User;     // existing user for edit
  isEdit?: boolean;
}

@Component({
  selector: 'app-add-edit-user-dialog',
  templateUrl: './add-edit-user-dialog.component.html',
  styleUrls: ['./add-edit-user-dialog.component.scss']
})
export class AddEditUserDialogComponent implements OnInit {
  userForm!: FormGroup;
  roles = Object.values(Role);

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<AddEditUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AddEditUserDialogData
  ) {}

  ngOnInit() {
    this.userForm = this.fb.group({
      id: [this.data.user?.id],
      name: [this.data.user?.name || '', Validators.required],
      email: [this.data.user?.email || '', [Validators.required, Validators.email]],
      password: [null],
      dob: [this.data.user?.dob || null],
      role: [this.data.user?.role || Role.USER],
      accountLocked: [this.data.user?.accountLocked || false],
      verified:[this.data.user?.verified || false],
    });
  }

  onSave() {
    if (this.userForm.invalid) return;
    const formValue = this.userForm.value as User;
    this.dialogRef.close(formValue);
  }

  onCancel() {
    this.dialogRef.close(null);
  }
}
