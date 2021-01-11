function sayHello(name) {
  console.log(`Hello ${name} !`)
  console.log('d')
}
function sayGoodbye(name) {
  console.log(`Goodbye ${name} !`)
  console.log(`d`)
}

// module.exports.sayHello = sayHello
module.exports = {
  sayHello
};