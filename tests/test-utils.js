function printStatus(requestParams, response, context, ee, next) {
    console.log(`${response.body}: ${response.statusCode}`);
    return next();
}

module.exports = {
    printStatus: printStatus
}