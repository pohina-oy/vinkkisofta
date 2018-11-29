
exports.up = function(knex, Promise) {
  return knex.schema.hasTable('bookmark_tags').then(exists => {
    if (!exists) {
      return knex.schema.createTable('bookmark_tags', t => {
        t.string('bookmarkId').references('bookmarks.id')
        t.string('tagId').references('tags.id')
      })
    }
  })
}

exports.down = function(knex, Promise) {
  return knex.schema.dropTableIfExists('bookmark_tags')
}
