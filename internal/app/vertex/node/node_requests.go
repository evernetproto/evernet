package node

type CreationRequest struct {
	Identifier  string `json:"identifier" binding:"required"`
	DisplayName string `json:"display_name" binding:"required"`
	Description string `json:"description"`
	Open        bool   `json:"open"`
}

type UpdateRequest struct {
	DisplayName string `json:"display_name"`
	Description string `json:"description"`
}

type OpenUpdateRequest struct {
	Open bool `json:"open"`
}
