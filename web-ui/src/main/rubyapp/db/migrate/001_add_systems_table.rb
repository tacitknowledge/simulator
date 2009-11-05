class AddSystemsTable < ActiveRecord::Migration
  def self.up
    create_table :systems do |t|
      t.string :name, :null => false
      t.text :description, :null => false
      t.string :script_language, :null => false
    end

  end

  def self.down
    drop_table :systems
  end
end
