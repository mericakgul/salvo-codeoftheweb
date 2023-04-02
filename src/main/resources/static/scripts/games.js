import {login, signup, logout, showPlayerUsername,fetchedGamesObject, loggedInPlayerUsername} from "./utilities/helpers.js";

const leaderboard = document.querySelector('#leaderboard');
const gamesList = document.getElementById("games-list");
const loggedInPlayerUsernameArea = document.getElementById("logged-in-player");
const loginBtn = document.querySelector('#login-btn');
const logoutBtn = document.querySelector('#logout-btn');
const signupBtn = document.querySelector('#signup-btn');
const createGameBtn =document.querySelector('#create-game');
const loginForm = document.querySelector('#login-form');
const warningToLogin = document.querySelector('#warning-to-login');
const infoAboutGames = document.querySelector('#info-about-games');
const fetchedGamesList = fetchedGamesObject['games'];

loginBtn.addEventListener('click', evt => login(evt));
signupBtn.addEventListener('click', evt => signup(evt)); // Normally we do not need evt parameter for signup since we are sending the json file to our endpoint but we are also logging in after signing up automatically. This is why we use evt parameter.

createGameBtn.addEventListener('click', () => createNewGame());

if (loggedInPlayerUsername) {
    showPlayerUsername(loggedInPlayerUsername, loggedInPlayerUsernameArea);
    loginForm.remove();
    warningToLogin.remove();
    infoAboutGames.setAttribute('style', 'visibility: visible');
    loggedInPlayerUsernameArea.setAttribute('style', 'visibility: visible');
    logoutBtn.setAttribute('style', 'visibility: visible');
    logoutBtn.addEventListener('click', logout);
    createGameBtn.removeAttribute('disabled');
}

const briefGameInfo = (games) => {
    return games.reduce((briefGameInfoList, game, index) => {
        const date = new Date(game['created']);
        const formattedDate = date.toLocaleString();
        const briefGameInfo = {
            'no': index + 1,
            'created-time': formattedDate,
            'first-player': game['gamePlayers'][0]['player']['username'],
            'second-player': game['gamePlayers'][1]?.['player']?.['username'] || '', // If there is a game, there must be created time and first player who is the creator but second player might not exist.
            'game-id': game['gameId']
        }
        briefGameInfoList.push(briefGameInfo);
        return briefGameInfoList;
    }, []);
}

briefGameInfo(fetchedGamesList).forEach(createGamesListTable);


const scoresOfPlayers = (games) => {
    const playerList = createPlayerListFromJson(games);
    return playerList.reduce((scoresOfPlayers, player, index) => {
        const playerResult = {
            'no': index + 1,
            'name': player,
            'total': getTotalScoreOfPlayer(player, games),
            'won': getTotalWinCountOfPlayer(player, games),
            'lost': getTotalLossCountOfPlayer(player, games),
            'tied': getTotalTieCountOfPlayer(player, games)
        }
        scoresOfPlayers.push(playerResult);
        return scoresOfPlayers;
    }, []).sort((firstPlayer, secondPlayer) => secondPlayer['total'] - firstPlayer['total']);
}

scoresOfPlayers(fetchedGamesList).forEach(createLeaderboardTable);

function createGamesListTable(briefGameInfo) {
    createTable(briefGameInfo, gamesList);
}

function createLeaderboardTable(player) {
    createTable(player, leaderboard);
}

function createTable(data, tableName) {
    const tableRow = document.createElement('tr');

    Object.keys(data).forEach(key => {
        const tableCell = document.createElement('td');
        const tableCellText = document.createTextNode(data[key]);
        tableCell.appendChild(tableCellText);
        tableRow.appendChild(tableCell);
    });

    if (tableName === gamesList) {
        createViewButton(tableRow, data);
        createJoinButton(tableRow, data);
    }
    tableName.appendChild(tableRow);
}

function createViewButton(tableRow, gameData) {
    const firstPlayer = gameData['first-player'];
    const secondPlayer = gameData['second-player'];
    const gameId = gameData['game-id'];
    const buttonCell = document.createElement('td');
    const button = createButtonBasics('View');
    buttonCell.appendChild(button);
    tableRow.appendChild(buttonCell);
    button.disabled = !loggedInPlayerUsername || ![firstPlayer, secondPlayer].includes(loggedInPlayerUsername);
    button.addEventListener('click', () => viewTheGame(gameId));
}

function createJoinButton(tableRow, gameData) {
    const firstPlayer = gameData['first-player'];
    const secondPlayer = gameData['second-player'];
    const gameId = gameData['game-id'];
    const buttonCell = document.createElement('td');
    const button = createButtonBasics('Join');
    buttonCell.appendChild(button);
    tableRow.appendChild(buttonCell);
    button.disabled = !loggedInPlayerUsername || !!secondPlayer || firstPlayer === loggedInPlayerUsername;
    button.addEventListener('click', () => joinTheGame(gameId));
}

function createButtonBasics(buttonText) {
    const button = document.createElement('button');
    button.classList.add('btn');
    button.classList.add('btn-primary');
    button.textContent = buttonText;
    return button;
}

function viewTheGame(gameId) {
    const gameClicked = fetchedGamesList.find(game => game['gameId'] === gameId);
    const gamePlayerOfLoggedInUser = gameClicked['gamePlayers']
        .find(gamePlayer => gamePlayer['player']['username'] === loggedInPlayerUsername);
    const gamePlayerId = gamePlayerOfLoggedInUser['id'];
    window.location.href = `/web/game.html?gp=${gamePlayerId}`;
}

function joinTheGame() {
 //TODO
}

function createNewGame() {
    fetch('/api/games', {
        method: 'POST'
    }).then(response => {
        if(response.status === 201) {
            return response.json();
        } else {
            alert('Game couldn\'t be created! Try again later.');
        }
    }).then(responseJSON => {
        alert('New game is successfully added.');
        window.location.href = `/web/game.html?gp=${responseJSON['gpid']}`
    });
}

function createPlayerListFromJson(games) {
    return games.reduce((playerList, {gamePlayers}) => {
        gamePlayers.forEach(({player}) => {
            if (!playerList.includes(player['username'])) {
                playerList.push(player['username']);
            }
        });
        return playerList;
    }, []);
}

function getTotalScoreOfPlayer(playerUsername, games) {
    return games.reduce((totalScore, {gamePlayers}) => {
        gamePlayers.forEach(gamePlayer => {
            if (gamePlayer['player']['username'] === playerUsername) {
                totalScore += gamePlayer['score'];
            }
        })
        return totalScore;
    }, 0);
}

function getTotalWinCountOfPlayer(playerUsername, games) {
    return resultCounter(playerUsername, games, 'win');
}

function getTotalTieCountOfPlayer(playerUsername, games) {
    return resultCounter(playerUsername, games, 'tie');
}

function getTotalLossCountOfPlayer(playerUsername, games) {
    return resultCounter(playerUsername, games, 'loss');
}

function resultCounter(playerUsername, games, resultType) {
    const score = resultType === 'win' ? 1.0 : resultType === 'tie' ? 0.5 : 0.0;
    return games.reduce((counter, {gamePlayers}) => {
        gamePlayers.forEach(gamePlayer => {
            if (gamePlayer['player']['username'] === playerUsername && gamePlayer['score'] === score) {
                counter += 1;
            }
        });
        return counter;
    }, 0);
}
