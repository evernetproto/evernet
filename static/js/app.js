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

function escapeHTML(str) {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
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

function getCurrentAdmin(success, error) {
    $.ajax({
        url: "/api/v1/admins/current",
        type: "GET",
        dataType: "json",
        contentType: "application/json",
        headers: getAdminHeaders(),
        success: success,
        error: error
    })
}

function changeCurrentAdminPassword(password, success, error) {
    $.ajax({
        url: "/api/v1/admins/current/password",
        type: "PUT",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            password: password,
        }),
        headers: getAdminHeaders(),
        success: success,
        error: error
    })
}

function addAdmin(identifier, success, error) {
    $.ajax({
        url: "/api/v1/admins",
        type: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            identifier: identifier,
        }),
        headers: getAdminHeaders(),
        success: success,
        error: error
    })
}

function listAdmins(page, size, success, error) {
    $.ajax({
        url: "/api/v1/admins",
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

function resetAdminPassword(identifier, success, error) {
    $.ajax({
        url: `/api/v1/admins/${identifier}/password`,
        type: "PUT",
        dataType: "json",
        contentType: "application/json",
        headers: getAdminHeaders(),
        success: success,
        error: error
    })
}

function deleteAdmin(identifier, success, error) {
    $.ajax({
        url: `/api/v1/admins/${identifier}`,
        type: "DELETE",
        dataType: "json",
        contentType: "application/json",
        headers: getAdminHeaders(),
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

function getNode(identifier, success, error) {
    $.ajax({
        url: `/api/v1/admins/nodes/${identifier}`,
        type: "GET",
        dataType: "json",
        contentType: "application/json",
        headers: getAdminHeaders(),
        success: success,
        error: error
    })
}

function updateNode(identifier, displayName, description, open, success, error) {
    $.ajax({
        url: `/api/v1/admins/nodes/${identifier}`,
        type: "PUT",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            display_name: displayName,
            description: description,
            open: open,
        }),
        headers: getAdminHeaders(),
        success: success,
        error: error
    })
}

function deleteNode(identifier, success, error) {
    $.ajax({
        url: `/api/v1/admins/nodes/${identifier}`,
        type: "DELETE",
        dataType: "json",
        contentType: "application/json",
        headers: getAdminHeaders(),
        success: success,
        error: error
    })
}

function resetNodeSigningKeys(identifier, success, error) {
    $.ajax({
        url: `/api/v1/admins/nodes/${identifier}/signing-keys`,
        type: "PUT",
        dataType: "json",
        contentType: "application/json",
        headers: getAdminHeaders(),
        success: success,
        error: error
    })
}
