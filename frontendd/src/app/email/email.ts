import { Component, type OnInit, inject } from "@angular/core"
import { CommonModule } from "@angular/common"
import { FormsModule, ReactiveFormsModule, FormBuilder, type FormGroup, Validators } from "@angular/forms"
import { HttpClient, HttpHeaders } from "@angular/common/http"
import { Token } from "../services/token/token"

interface UpdatedMsgDTO {
  content?: string
  scheduledDate?: string
  recipientEmail?: string
  attachmentPath?: string
}

interface TimeCapsuleMessage {
  id: number
  subject: string
  content: string
  recipientEmail: string
  scheduledDate: string
  status: string
  attachmentPath?: string
  createdAt: string
  updatedAt: string
}

interface AIResponse {
  email: string
}

@Component({
  selector: "app-time-capsule-message",
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: "./email.html",
  styleUrls: ["./email.scss"],
})
export class Email implements OnInit {
  private readonly API_BASE_URL = "http://localhost:8080"

  private fb = inject(FormBuilder)
  private http = inject(HttpClient)
  private tokenService = inject(Token)

  messageForm!: FormGroup
  updateForm!: FormGroup

  isSubmitting = false
  isGeneratingAI = false
  isUpdating = false
  focusedFields: { [key: string]: boolean } = {}

  // AI
  aiPrompt = ""
  aiResponse = ""

  // Message management
  messages: TimeCapsuleMessage[] = []
  selectedMessage: TimeCapsuleMessage | null = null
  isEditMode = false

  // File handling
  selectedFile: File | null = null

  ngOnInit(): void {
    this.initializeForms()
    this.loadMessages()
  }

  private initializeForms(): void {
    this.messageForm = this.fb.group({
      subject: ["", [Validators.required, Validators.minLength(3)]],
      content: ["", [Validators.required, Validators.minLength(10)]],
      recipientEmail: ["", [Validators.required, Validators.email]],
      scheduledDate: ["", Validators.required],
    })

    this.updateForm = this.fb.group({
      content: [""],
      recipientEmail: ["", Validators.email],
      scheduledDate: [""],
      attachmentPath: [""],
    })
  }

  setFieldFocus(fieldName: string, focused: boolean): void {
    this.focusedFields[fieldName] = focused
  }

  isFieldFocused(fieldName: string): boolean {
    return this.focusedFields[fieldName] || false
  }

  // File handling methods
  onFileSelected(event: any): void {
    const file = event.target.files[0]
    if (file) {
      this.selectedFile = file
    }
  }

  clearSelectedFile(): void {
    this.selectedFile = null
    // Reset file input
    const fileInput = document.querySelector('input[type="file"]') as HTMLInputElement
    if (fileInput) {
      fileInput.value = ""
    }
  }

  // AI integration
  async generateAIResponse(): Promise<void> {
    if (!this.aiPrompt.trim()) {
      alert("Please enter a prompt for AI generation")
      return
    }

    this.isGeneratingAI = true
    try {
      if (!this.isAuthenticated()) {
        alert("Your session has expired. Please login again.")
        return
      }

      const headers = this.getAuthHeaders()
      const response = await this.http
        .post<AIResponse>(`${this.API_BASE_URL}/ai/email`, { input: this.aiPrompt }, { headers })
        .toPromise()

      if (response?.email) {
        this.aiResponse = response.email
        this.messageForm.patchValue({ content: response.email })
      }
    } catch (error: any) {
      console.error("[v0] Error generating AI response:", error)
      if (error.status === 401) {
        alert("Authentication failed. Please login again.")
      } else if (error.status === 403) {
        alert("Access forbidden. You don't have permission to use AI generation.")
      } else if (error.status === 500) {
        alert("Server error. Please try again later.")
      } else if (error.message?.includes("Authentication required")) {
        alert(error.message)
      } else {
        alert("Error generating AI response. Please check your connection and try again.")
      }
    } finally {
      this.isGeneratingAI = false
    }
  }

  useAIResponse(): void {
    if (this.aiResponse) {
      this.messageForm.patchValue({ content: this.aiResponse })
    }
  }

  clearAIResponse(): void {
    this.aiResponse = ""
    this.aiPrompt = ""
  }

  // CRUD
  async loadMessages(): Promise<void> {
    try {
      const headers = this.getAuthHeaders()
      const response = await this.http
        .get<TimeCapsuleMessage[]>(`${this.API_BASE_URL}/timeCapsuleMessage`, { headers })
        .toPromise()

      this.messages = response || []
    } catch (error: any) {
      console.error("[v0] Error loading messages:", error)
      if (error.status === 401) {
        alert("Authentication required. Please login to view messages.")
      } else if (error.status === 0) {
        alert("Cannot connect to server. Please check if the backend is running on port 8080.")
      } else {
        alert("Error loading messages. Please try again.")
      }
    }
  }

  async onSubmit(): Promise<void> {
    if (!this.messageForm.valid) {
      this.markFormGroupTouched()
      return
    }

    if (!this.isAuthenticated()) {
      alert("You must be logged in to save a message.")
      return
    }

    this.isSubmitting = true
    try {
      // Debug: Check authentication
      console.log("Token available:", !!this.tokenService.token)
      console.log("Is authenticated:", this.isAuthenticated())

      // Create FormData for multipart request
      const formData = new FormData()

      // Add message data as JSON blob for @RequestPart("message")
      const messageData = {
        subject: this.messageForm.value.subject,
        content: this.messageForm.value.content,
        recipientEmail: this.messageForm.value.recipientEmail,
        scheduledDate: this.messageForm.value.scheduledDate,
      }

      // Create JSON blob with correct content type for Spring Boot
      formData.append(
        "message",
        new Blob([JSON.stringify(messageData)], {
          type: "application/json",
        }),
      )

      // Add file if selected for @RequestPart("file")
      if (this.selectedFile) {
        formData.append("file", this.selectedFile)
      }

      const token = this.tokenService.token
      if (!token) {
        throw new Error("Authentication required - please login first")
      }

      // Validate token
      try {
        const payload = JSON.parse(atob(token.split(".")[1]))
        if (payload.exp * 1000 < Date.now()) {
          throw new Error("Token expired - please login again")
        }
      } catch {
        throw new Error("Invalid token - please login again")
      }

      // Don't set Content-Type - browser will set it with boundary for multipart
      const headers = new HttpHeaders({
        Authorization: `Bearer ${token}`,
      })

      await this.http.post(`${this.API_BASE_URL}/timeCapsuleMessage/save`, formData, { headers }).toPromise()

      alert("Time capsule message created successfully!")
      this.resetForm()
      this.loadMessages()
    } catch (error: any) {
      console.error("[v0] Error saving message:", error)
      if (error.status === 401) {
        alert("Authentication failed. Please check your login credentials.")
      } else if (error.status === 400) {
        alert("Invalid data format. Please check your input and try again.")
      } else if (error.status === 413) {
        alert("File too large. Please select a smaller file.")
      } else if (error.status === 0) {
        alert("Cannot connect to server. Please check if the backend is running.")
      } else {
        alert(`Error creating message: ${error.message || "Unknown error"}`)
      }
    } finally {
      this.isSubmitting = false
    }
  }

  selectMessage(message: TimeCapsuleMessage): void {
    this.selectedMessage = message
    this.isEditMode = true
    this.updateForm.patchValue({
      content: message.content,
      recipientEmail: message.recipientEmail,
      scheduledDate: message.scheduledDate.split("T")[0],
      attachmentPath: message.attachmentPath || "",
    })
  }

  async updateMessage(): Promise<void> {
    if (!this.selectedMessage) return

    if (!this.isAuthenticated()) {
      alert("You must be logged in to update messages.")
      return
    }

    this.isUpdating = true
    try {
      const updateData: UpdatedMsgDTO = { ...this.updateForm.value }
      const headers = this.getAuthHeaders()
      await this.http
        .put(`${this.API_BASE_URL}/timeCapsuleMessage/updateMsg/${this.selectedMessage.id}`, updateData, { headers })
        .toPromise()

      alert("Message updated successfully!")
      this.cancelEdit()
      this.loadMessages()
    } catch (error: any) {
      console.error("[v0] Error updating message:", error)
      if (error.status === 401) {
        alert("Authentication required. Please login to update messages.")
      } else if (error.status === 404) {
        alert("Message not found. It may have been deleted.")
      } else {
        alert("Error updating message. Please try again.")
      }
    } finally {
      this.isUpdating = false
    }
  }

  async deleteMessage(id: number): Promise<void> {
    if (!confirm("Are you sure you want to delete this message?")) return

    if (!this.isAuthenticated()) {
      alert("You must be logged in to delete messages.")
      return
    }

    try {
      const headers = this.getAuthHeaders()
      await this.http.delete(`${this.API_BASE_URL}/timeCapsuleMessage/${id}`, { headers }).toPromise()

      alert("Message deleted successfully!")
      this.loadMessages()
      if (this.selectedMessage?.id === id) {
        this.cancelEdit()
      }
    } catch (error: any) {
      console.error("[v0] Error deleting message:", error)
      if (error.status === 401) {
        alert("Authentication required. Please login to delete messages.")
      } else if (error.status === 404) {
        alert("Message not found. It may have already been deleted.")
      } else {
        alert("Error deleting message. Please try again.")
      }
    }
  }

  cancelEdit(): void {
    this.selectedMessage = null
    this.isEditMode = false
    this.updateForm.reset()
  }

  resetForm(): void {
    this.messageForm.reset()
    this.focusedFields = {}
    this.clearAIResponse()
    this.clearSelectedFile()
  }

  markFormGroupTouched(): void {
    Object.keys(this.messageForm.controls).forEach((key) => {
      this.messageForm.get(key)?.markAsTouched()
    })
  }

  getFieldError(fieldName: string, formGroup: FormGroup = this.messageForm): string {
    const field = formGroup.get(fieldName)
    if (field?.errors && field.touched) {
      if (field.errors["required"]) return `${this.getFieldDisplayName(fieldName)} is required`
      if (field.errors["minlength"]) return `${this.getFieldDisplayName(fieldName)} is too short`
      if (field.errors["email"]) return "Please enter a valid email address"
    }
    return ""
  }

  private getFieldDisplayName(fieldName: string): string {
    const displayNames: Record<string, string> = {
      subject: "Subject",
      content: "Content",
      recipientEmail: "Recipient Email",
      scheduledDate: "Scheduled Date",
      attachmentPath: "Attachment Path",
    }
    return displayNames[fieldName] || fieldName
  }

  isFieldInvalid(fieldName: string, formGroup: FormGroup = this.messageForm): boolean {
    const field = formGroup.get(fieldName)
    return !!(field?.invalid && field.touched)
  }

  getFormProgress(): number {
    const totalFields = Object.keys(this.messageForm.controls).length
    const validFields = Object.values(this.messageForm.controls).filter(
      (control) => control.valid && !!control.value,
    ).length
    return (validFields / totalFields) * 100
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString()
  }

  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case "pending":
        return "status-pending"
      case "sent":
        return "status-sent"
      case "failed":
        return "status-failed"
      default:
        return "status-unknown"
    }
  }

  isAuthenticated(): boolean {
    const token = this.tokenService.token
    if (!token) return false

    try {
      const payload = JSON.parse(atob(token.split(".")[1]))
      return payload.exp * 1000 > Date.now()
    } catch {
      return false
    }
  }

  private getAuthHeaders(): HttpHeaders {
    const token = this.tokenService.token
    if (!token) throw new Error("Authentication required - please login first")

    try {
      const payload = JSON.parse(atob(token.split(".")[1]))
      if (payload.exp * 1000 < Date.now()) {
        throw new Error("Token expired - please login again")
      }
    } catch {
      throw new Error("Invalid token - please login again")
    }

    return new HttpHeaders({
      "Content-Type": "application/json",
      Accept: "application/json",
      Authorization: `Bearer ${token}`,
    })
  }
}
