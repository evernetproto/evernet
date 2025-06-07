package node

type CreationRequest struct {
	Identifier  string `json:"identifier" binding:"required"`
	Name        string `json:"name" binding:"required"`
	Description string `json:"description"`
	Open        bool   `json:"open"`
}
