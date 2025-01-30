import { Component, OnInit } from '@angular/core';
import { saveAs } from 'file-saver';
import {User, UserService} from "../users.service";
import {
  AddEditUserDialogComponent,
  AddEditUserDialogData
} from "../add-edit-user-dialog/add-edit-user-dialog.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss']
})
export class UsersListComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  searchText: string = '';

  constructor(private userService: UserService,private dialog: MatDialog) {}

  ngOnInit(): void {
    this.fetchUsers();
  }

  fetchUsers() {
    this.userService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.filteredUsers = data;
      },
      error: (err) => {
        console.error('Error fetching users:', err);
      }
    });
  }

  onSearch() {
    const text = this.searchText.toLowerCase();
    this.filteredUsers = this.users.filter(user =>
      user.name.toLowerCase().includes(text) ||
      user.email.toLowerCase().includes(text)
    );
  }

  onAddUser() {
    const dialogRef = this.dialog.open(AddEditUserDialogComponent, {
      width: '500px',
      data: { isEdit: false }
    });

    dialogRef.afterClosed().subscribe((result: User | null) => {
      if (result) {
        this.userService.createUser(result).subscribe(() => {
          this.fetchUsers()
        });
      }
    });
  }

  onEditUser(user: User) {
    const dialogRef = this.dialog.open(AddEditUserDialogComponent, {
      width: '500px',
      data: { user: { ...user }, isEdit: true } as AddEditUserDialogData
    });

    dialogRef.afterClosed().subscribe((updated: User | null) => {
      if (updated) {
        if (!updated.id) return;
        this.userService.updateUser(updated.id, updated).subscribe(() => {
          this.fetchUsers()
        });
      }
    });
  }

  onDelete(userId?: number) {
    if (!userId) return;
    if (!confirm('Are you sure you want to delete this user?')) return;

    this.userService.deleteUser(userId).subscribe({
      next: () => {
        this.users = this.users.filter(u => u.id !== userId);
        this.filteredUsers = this.filteredUsers.filter(u => u.id !== userId);
      },
      error: (err) => alert('Delete failed.')
    });
  }

  exportToCSV() {
    const headers = ['ID', 'Name', 'Email', 'Role', 'Verified', 'Locked'];
    const rows = this.filteredUsers.map(u => [
      u.id, u.name, u.email, u.role, u.verified, u.accountLocked
    ]);

    let csvContent = headers.join(',') + '\n';
    rows.forEach(row => {
      csvContent += row.join(',') + '\n';
    });

    // Convert to Blob
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' });
    saveAs(blob, 'users_export.csv');
  }

  userImageUrl(user: User): string {
    // If your user entity has an `avatarUrl`, use it; otherwise default
    return './assets/icons/profile.png';
  }

}
