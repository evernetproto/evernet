package api

import (
	"strconv"

	"github.com/gin-gonic/gin"
)

func Page(c *gin.Context) (int, int) {
	pageString := c.DefaultQuery("page", "0")
	sizeString := c.DefaultQuery("size", "50")

	page, err := strconv.Atoi(pageString)

	if err != nil {
		page = 0
	}

	size, err := strconv.Atoi(sizeString)

	if err != nil {
		size = 50
	}

	return page, size
}
