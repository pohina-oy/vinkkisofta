
exports.up = function(knex, Promise) {
  return knex.schema.hasTable('bookmarks').then(exists => {
    if (!exists) {
      return knex.schema.createTable('bookmarks', t => {
        t.string('id').primary()
        t.string('title')
        t.string('url')
        t.string('author')
      })
    }
  })
  
}

exports.down = function(knex, Promise) {
  return knex.schema.dropTableIfExists('bookmarks')
}
