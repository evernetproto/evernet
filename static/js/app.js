function displaySuccess(parent, message) {
    parent.html(`<div class="alert alert-success">${message}</div>`);
}

function displayError(parent, message) {
    parent.html(`<div class="alert alert-danger">${message}</div>`);
}

function getAdminHeaders() {
    return {
        Authorization: "Bearer " + localStorage.getItem("adminToken"),
    }
}

function isInitialized(success, error) {
    $.ajax({
        url: "/init",
        type: "GET",
        dataType: "json",
        contentType: "application/json",
        success: success,
        error: error
    })
}

function initAdmin(identifier, password, vertexEndpoint, success, error) {
    $.ajax({
        url: "/api/v1/admins/init",
        type: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            identifier: identifier,
            password: password,
            vertex_endpoint: vertexEndpoint,
        }),
        success: success,
        error: error
    })
}

function getAdminToken(identifier, password, success, error) {
    $.ajax({
        url: "/api/v1/admins/token",
        type: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            identifier: identifier,
            password: password,
        }),
        success: success,
        error: error
    })
}

function addNode(identifier, displayName, description, open, success, error) {
    $.ajax({
        url: "/api/v1/admins/nodes",
        type: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            identifier: identifier,
            display_name: displayName,
            description: description,
            open: open,
        }),
        headers: getAdminHeaders(),
        success: success,
        error: error
    })
}

function listNodes(page, size, success, error) {
    $.ajax({
        url: "/api/v1/admins/nodes",
        type: "GET",
        dataType: "json",
        contentType: "application/json",
        data: {
            page: page,
            size: size,
        },
        headers: getAdminHeaders(),
        success: success,
        error: error
    })
}
