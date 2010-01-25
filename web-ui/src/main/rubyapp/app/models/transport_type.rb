# == Schema Information
#
# Table name: transport_types
#
#  id         :integer(10)     not null, primary key
#  name       :string(255)     not null
#  class_name :string(255)     not null
#  created_at :datetime
#  updated_at :datetime
#

class TransportType < ActiveRecord::Base
#  has_many :transports
  
  validates_presence_of :name, :class_name

  #Retrieves class name of a transport given a transport name
  def self.get_transport_class_name_by_name(name)
    transport_type = TransportType.find(:first, :conditions => ["name = ?", name], :select => "class_name")
    if transport_type
      return transport_type.class_name
    end
  end

end
