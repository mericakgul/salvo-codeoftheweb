import {domainUrl} from "./constants.js";
const messageBox = document.querySelector('#login-result');
const usernameInput = document.querySelector('#username');
const passwordInput = document.querySelector('#password');

export const fetchJson = url =>
    fetch(domainUrl + url).then(response => {
        if (response.ok) {
            return response.json();
        } else if (response.status === 401) {
            alert('You are not authorized to see the game details!');
            window.location.href = '/web/games.html';
        } else {
            throw new Error('error: ' + response.statusText);
        }
    });

export function login(evt) {
    if (areFieldsValid()) {
        sendLoginRequest(evt);
    }
}

export function signup(evt) {
    if (areFieldsValid()) {
        sendSignupRequest(evt);
    }
}

export function logout() {
    fetch('/api/logout', {
        method: 'POST'
    }).then(response => {
        if (response.ok && response.status === 200) {
            alert('Successfully logged out!');
            window.location.href = '/web/games.html';
        }
    })
}

export const fetchedGamesObject = await fetchJson('/api/games'); //Top level await. No need to be in async function.
export const loggedInPlayerUsername = fetchedGamesObject['player']['username'];


export function showPlayerUsername (username, loggedInPlayerUsernameArea) {
    const usernameText = document.createTextNode(username);
    loggedInPlayerUsernameArea.appendChild(usernameText);
}

function areFieldsValid() {
    if (!usernameInput.value || !passwordInput.value) {
        alert('Please fill both username and password fields in.');
        return;
    }
    if (!isValidEmail(usernameInput.value)) {
        alert('Please fill a valid email address in.');
        return;
    }
    return true;
}

// While sending log in request we send the file in application/x-www-form-urlencoded format not json format. This is why body value here is different from the body value in signUp request.
// Here we use evt parameter, and we reach the form with evt.target.form value. Afterwards, username and password variables are assigned to form.username.value and form.password.value values respectively.
// How does it understand which input fields in the form are for username and password? HtmlFormElement checks the input elements with 'name' (or 'id') attribute valued as username and password and takes the input values(provided by user) in these inputs and assigns it in body part of the request.
// Another thing is the login request done to /api/login doesn't have any endpoint we created manually. It is provided in the web security configuration ( http.formLogin().loginProcessingUrl("/api/login") )
function sendLoginRequest(evt) {
    const form = evt.target.form;
    fetch('/api/login', {
        method: 'POST',
        headers: {
            "Content-type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: `username=${form.username.value}&password=${form.password.value}`
    }).then(response => {
        if (response.ok && response.status === 200) {
            alert('You are successfully logged in!')
            history.go(0);
        } else {
            messageBox.innerText = 'login failure';
        }
    });
}

// Here in signUp request body we do not use evt parameter and HtmlFormElement(evt.target.form) because our format is application/json,
// so we just find the username and password input fields by using query selectors and take the input values in it and place them to our json in the body part
// and make a signup POST request to the endpoint that we manually created which is'/api/players'
function sendSignupRequest(evt) {
    fetch('/api/players', {
        method: 'POST',
        body: JSON.stringify({username: usernameInput.value, password: passwordInput.value}),
        headers: new Headers({
            'Content-Type': 'application/json'
        })
    }).then((response) => {
        if (response.ok && response.status === 200) {
            alert('You are successfully signed up!');
            sendLoginRequest(evt);
        } else if (response.status === 403) {
            alert('This username is in use, choose another one.')
        }
    });
}

function isValidEmail(email) {
    const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}
