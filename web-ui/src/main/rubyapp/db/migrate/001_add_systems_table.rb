class AddSystemsTable < ActiveRecord::Migration
  def self.up
    create_table :systems do |t|
      t.string :name, :null => false
      t.text :description
      t.string :script_language, :null => false, :default => 'JavaScript'

      t.timestamps
    end

  end

  def self.down
    drop_table :systems
  end
end
