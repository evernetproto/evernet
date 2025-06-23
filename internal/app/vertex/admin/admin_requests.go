package admin

type InitRequest struct {
	Identifier        string `json:"identifier" binding:"required"`
	Password          string `json:"password" binding:"required"`
	VertexEndpoint    string `json:"vertex_endpoint" binding:"required"`
	VertexIdentifier  string `json:"vertex_identifier" binding:"required"`
	VertexDisplayName string `json:"vertex_display_name" binding:"required"`
	VertexDescription string `json:"vertex_description" binding:"required"`
}

type TokenRequest struct {
	Identifier string `json:"identifier" binding:"required"`
	Password   string `json:"password" binding:"required"`
}

type PasswordChangeRequest struct {
	Password string `json:"password" binding:"required"`
}

type AdditionRequest struct {
	Identifier string `json:"identifier" binding:"required"`
}
