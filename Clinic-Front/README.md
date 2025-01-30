# EventManager Frontend

An **Angular** application that serves as the admin and user-facing interface for the EventManager platform. Implements role-based UI (ADMIN/MANAGER see an admin dashboard; normal USER sees a landing page of events).

## Key Features

1. **Authentication & Role-Based Guards**
  - JWT-based login & Google OAuth2 integration.
  - RoleGuard restricts certain routes to `ADMIN` or `MANAGER`.

2. **Admin Dashboard**
  - **Navbar** and **Sidebar** with menu items:
    - **Users**: Manage user CRUD, lock/unlock accounts, reset attempts.
    - **Events**: Create, update, delete events (only for ADMIN/MANAGER).
    - **Rooms**: Manage rooms.
  - **Stats**: Quick stats with animated counters (total events, upcoming events, etc.).

3. **Landing Page (User)**
  - Displays list of events as cards.
  - Participating in events if logged in with `USER` role.
  - Searching, filters, etc.

4. **Forgot Password & OTP**
  - Forms to handle OTP verification, forgot password, and reset password flows.

5. **Responsive Design**
  - Uses **Tailwind CSS** + optionally **Bootstrap** or **Angular Material** components for a modern UI.

6. **Animations**
  - Simple fade-in, hover effects, and incrementing numeric stats (count-up animation).

## Tech Stack

- **Angular 17+**
- **TypeScript**
- **Tailwind CSS** and/or **Bootstrap** for styling
- **Angular Material** for dialogs, forms, etc.
- **ngx-toastr** for toast notifications
- **jwt-decode** for token parsing

## Project Structure (Highlights)

eventmanager-frontend/ ┣ src/ ┃ ┣ app/ ┃ ┃ ┣ admin/ # Admin module components (dashboard, users list, etc.) ┃ ┃ ┣ auth/ # Auth components (login, signup, forgot-pass, etc.) ┃ ┃ ┣ core/ # Services, guards, interceptors ┃ ┃ ┣ pages/ # Common pages (home, landing, etc.) ┃ ┃ ┗ app-routing.module.ts ┃ ┣ environments/ ┃ ┗ styles.css or styles.scss ┗ package.json


## Setup & Run

1. **Clone** the repository:
   ```bash
   git clone https://github.com/YourUsername/eventmanager-frontend.git
   cd eventmanager-frontend 
   ````

2. **Install Dependencies:**
    ````bash
      npm install
    ````
3. **Configure environment:**

    In ``src/environments/environment.ts``, set ``apiBaseUrl`` to your backend URL, e.g.:
    ````ts
   export const environment = {
    production: false,
       apiBaseUrl: 'http://localhost:8083/api'  // or wherever your backend runs
    };
    ````
4. **Run:**
    ````bash
    ng serve
   ````
The app is accessible at http://localhost:4200.

5. **Login & Testing:**
- Signup or use an admin user to see the admin dashboard.
- Normal user sees the landing events page.


## Notable Modules & Components
- **auth** Module:
    - LoginComponent: Email+password or Google OAuth2.
    - SignupComponent: OTP flow.
    - ForgotPasswordComponent & ResetPasswordComponent.
- **admin** Module:
  - AdminDashboardComponent: Renders the navbar, sidebar, and a <router-outlet> for child routes.
  - UsersComponent: Grid/list of users, add/edit user dialog, search, export, etc.
  - EventsComponent & RoomsComponent: CRUD for events/rooms.

- **core/services**:

AuthService: JWT storage, role decode, login & logout logic.
UserService: Makes calls to backend /api/users.
EventService: Calls backend /api/events.
NotificationService: Wraps ngx-toastr.
Guards:

AuthGuard: Checks if token is present.
RoleGuard: Checks if user’s role is in [ADMIN, MANAGER].
Usage
Login as an ADMIN or MANAGER to see the Admin Dashboard with “Users”, “Events”, “Rooms” sections.
Normal USER only sees the Landing with event cards.
Screenshots (Optional)
(Include some screenshots or GIFs of the admin dashboard, user list, etc.)

Contributing
Fork & create feature branches.
Submit a PR with a thorough description.
License
MIT License. See LICENSE for details.

Contact
Author: Your Name
Email: your.email@example.com
rust
Copier le code
Thanks for exploring EventManager Frontend!
markdown
Copier le code

---

### Tips

- Replace **YourUsername**, **Your Name**, or **your.email@example.com** with your actual info.
- If you have separate **LICENSE** files, you can reference them in the READMEs.
- Add **screenshots** or **GIFs** to each README to really bring them to life on your portfolio.

With these two READMEs, visitors to your GitHub repos will clearly see how to **install**, **run**, and **understand** your full-stack EventManager application. Good luck showcasing your project!





