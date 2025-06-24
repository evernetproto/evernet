package address

import (
	"errors"
	"fmt"
	"strings"
)

type NodeAddress struct {
	Identifier     string `json:"identifier"`
	VertexEndpoint string `json:"vertex_endpoint"`
}

func (n *NodeAddress) ToString() string {
	return fmt.Sprintf("%s/%s", n.VertexEndpoint, n.Identifier)
}

func NodeAddressFromString(nodeAddress string) (*NodeAddress, error) {
	components := strings.Split(nodeAddress, "/")

	if len(components) != 2 {
		return nil, errors.New("invalid node address")
	}

	identifier := components[1]
	vertexEndpoint := components[0]

	return &NodeAddress{
		Identifier:     identifier,
		VertexEndpoint: vertexEndpoint,
	}, nil
}
