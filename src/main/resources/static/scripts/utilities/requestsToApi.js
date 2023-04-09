import {domainUrl} from "./constants.js";

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

export const fetchedGamesObject = await fetchJson('/api/games'); //Top level await. No need to be in async function.
export const loggedInPlayerUsername = fetchedGamesObject['player']['username'];

export function joinGameRequest(gameId){
    return fetch(`/api/game/${gameId}/players`, {
        method: 'POST'
    });
}

export async function createNewGameRequest() {
    fetch('/api/games', {
        method: 'POST'
    }).then(response => {
        if (response.status === 201) {
            return response.json();
        } else {
            throw new Error('Game couldn\'t be created! Try again later.');
        }
    }).then(responseJSON => {
        alert('New game is successfully added.');
        window.location.href = `/web/game.html?gp=${responseJSON['gpid']}`;
    }).catch(error => alert(error.message));
}

export function sendShips(jsonBody) {

    fetch('/api/games/players/12/ships', {
        method: 'POST',
        body: jsonBody,
        headers: new Headers({
            'Content-Type': 'application/json'
        })
    }).then((response) => {
        if(response.ok && response.status === 201){
            alert('Ships have been placed');
            window.location.href = '/web/game.html?gp=12';
        } else {
            return response.json().then(error => {
                throw new Error(error.message);
            });
        }
    }).catch(error => alert(error.message));
}