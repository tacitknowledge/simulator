class AddScenariosTable < ActiveRecord::Migration
  def self.up
    create_table :scenarios do |t|
      t.string :conversationId, :null => false
      t.string :name, :null => false
      t.text :criteriaScript, :null => false
      t.text :executionScript, :null => false
      t.boolean :isActive, :default => false
    end
  end

  def self.down
    drop_table :scenarios
  end
end
